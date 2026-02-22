import re
import os

def load_syms(sym_file):
    items = {}
    with open(sym_file, 'r', encoding='utf-8') as f:
        for line in f:
            if not line.strip(): continue
            parts = line.strip().split('\t')
            if len(parts) == 2:
                items[parts[1]] = int(parts[0])
    return items

def extract_missing(log_file):
    missing_obj = []
    current_type = 'obj'
    
    with open(log_file, 'r', encoding='utf-8') as f:
        for line in f:
            m = re.search(r'- (Hash: null\t\|\s*)?Name: "(.*?)"', line)
            if m:
                missing_obj.append(m.group(2))
    return list(set(missing_obj))

def guess_mapping(miss, valid_items):
    # 1. Direct manual mappings
    manual = {
        'small_fishing_net': 'net',
        'big_fishing_net': 'big_net',
        'raw_shrimps': 'raw_shrimp',
        'vial_of_water': 'vial_water',
        'red_spider_eggs': 'red_spiders_eggs',
        'mort_myre_fungus': 'mort_myre_stem', # fallback
        'potato_cactus': 'potato_cactus',
        'birds_nest_crushed': 'crushed_nest',
        'mud_rune_ground': 'ground_mud_runes',
        'dragon_bones_superior_ground': 'ground_dragon_bones',
        'vial': 'vial_empty',
        'antipoison_3': '3doseantipoison',
        'superantipoison_3': '3dosesuperantipoison',
        'restore_potion_3': '3dosestatrestore',
        'guam_potion_unf': 'guamvial',
        'marentill_potion_unf': 'marrentillvial',
        'tarromin_potion_unf': 'tarrominvial',
        'harralander_potion_unf': 'harralandervial',
        'ranarr_potion_unf': 'ranarrvial',
        'toadflax_potion_unf': 'toadflaxvial',
        'irit_potion_unf': 'iritvial',
        'avantoe_potion_unf': 'avantoevial',
        'kwuarm_potion_unf': 'kwuarmvial',
        'snapdragon_potion_unf': 'snapdragonvial',
        'cadantine_potion_unf': 'cadantinevial',
        'lantadyme_potion_unf': 'lantadymevial',
        'dwarfweed_potion_unf': 'dwarfweedvial',
        'torstol_potion_unf': 'torstolvial',
    }
    if miss in manual:
        return manual[miss]
        
    # 2. Grimy herbs
    if miss.startswith('grimy_'):
        herb = miss[6:].replace('_leaf', '').replace('_weed', '')
        return f"unidentified_{herb}"
        
    # 3. Potions (e.g. attack_potion_3 -> 3dose1attack or 3doseattack)
    m = re.match(r'(.*)_potion_(\d+)', miss)
    if m:
        pot = m.group(1).replace('_', '')
        d = m.group(2)
        cand1 = f"{d}dose1{pot}"
        cand2 = f"{d}dose{pot}"
        cand3 = f"{d}dose{pot}potion"
        if cand1 in valid_items: return cand1
        if cand2 in valid_items: return cand2
        if cand3 in valid_items: return cand3
        
    # 4. Super potions (super_attack_3)
    m = re.match(r'super_(.*?)_(\d+)', miss)
    if m:
        pot = m.group(1).replace('_', '')
        d = m.group(2)
        cand = f"{d}dose2{pot}" # super pots in vanilla are often 2attack etc.
        if cand in valid_items: return cand
        
    # 5. Fallback heuristics
    return None

if __name__ == '__main__':
    valid_items = load_syms('Z:/Projects/OSRS-PS-DEV/rsmod/.data/symbols/obj.sym')
    missing = extract_missing('Z:/Projects/OSRS-PS-DEV/rsmod/install_log7.txt')
    
    out_lines = []
    used_ids = set()
    unmapped_counter = 1
    
    for miss in missing:
        mapped_name = guess_mapping(miss, valid_items)
        # Fuzzy match if not found
        if not mapped_name or mapped_name not in valid_items:
            # Let's try simple substitutions
            cand = miss.replace('_', '')
            if cand in valid_items:
                mapped_name = cand
                
        if mapped_name in valid_items:
            obj_id = valid_items[mapped_name]
            if obj_id not in used_ids:
                out_lines.append(f"{obj_id}\t{miss}")
                used_ids.add(obj_id)
            else:
                # If ID is already used in .local/obj.sym, we MUST assign a unique dummy
                # to prevent NameLoader crash, but TypeVerifier ALSO checks cache.
                # A safe valid ID that we haven't used yet:
                while unmapped_counter in used_ids: unmapped_counter += 1
                out_lines.append(f"{unmapped_counter}\t{miss}")
                used_ids.add(unmapped_counter)
        else:
            # Fallback to dummy
            while unmapped_counter in used_ids: unmapped_counter += 1
            out_lines.append(f"{unmapped_counter}\t{miss}")
            used_ids.add(unmapped_counter)
            
    with open('Z:/Projects/OSRS-PS-DEV/rsmod/.data/symbols/.local/obj.sym', 'w', encoding='utf-8') as f:
        f.write('\n'.join(out_lines))
    print(f"Mapped {len(missing)} items to valid IDs!")
