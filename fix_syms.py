import sys
import re
import os
from difflib import get_close_matches

def extract_missing_by_type(log_file):
    missing_obj = []
    missing_seq = []
    current_type = 'obj'
    
    with open(log_file, 'r', encoding='utf-8') as f:
        for line in f:
            if "The following references use names that are not defined in a .sym file" in line:
                if len(missing_obj) > 0:
                    current_type = 'seq'
                continue
                
            m = re.search(r'- (Hash: null\t\|\s*)?Name: "(.*?)"', line)
            if m:
                name = m.group(2)
                if current_type == 'obj':
                    missing_obj.append(name)
                else:
                    missing_seq.append(name)
                    
    return list(set(missing_obj)), list(set(missing_seq))

def map_symbols(missing_names, sym_file, out_file, manual_maps):
    valid_syms = {}
    if os.path.exists(sym_file):
        with open(sym_file, 'r', encoding='utf-8') as f:
            for line in f:
                line = line.strip()
                if not line: continue
                parts = line.split('\t')
                if len(parts) == 2:
                    valid_syms[parts[1]] = parts[0]
                    
    valid_names = list(valid_syms.keys())
    
    out_lines = []
    for miss in missing_names:
        mapped = None
        if miss in manual_maps:
            mapped = manual_maps[miss]
        else:
            test_miss = miss
            if test_miss.startswith("grimy_"):
                test_miss = "unidentified_" + test_miss[6:].replace("_leaf", "").replace("_weed", "")
            if test_miss.endswith("_potion_unf"):
                test_miss = test_miss.replace("_potion_unf", "vial")
                
            closest = get_close_matches(test_miss, valid_names, n=1, cutoff=0.5)
            if closest:
                mapped = closest[0]
            else:
                closest = get_close_matches(miss, valid_names, n=1, cutoff=0.3)
                if closest:
                    mapped = closest[0]
                    
        if mapped and mapped in valid_syms:
            out_lines.append(f"{valid_syms[mapped]}\t{miss}")
        else:
            print(f"FAILED TO MATCH: {miss}")
            out_lines.append(f"0\t{miss}")
            
    os.makedirs(os.path.dirname(out_file), exist_ok=True)
    with open(out_file, 'w', encoding='utf-8') as f:
        f.write('\n'.join(out_lines))
    print(f"Wrote {len(out_lines)} mappings to {out_file}")

if __name__ == '__main__':
    log_file = 'Z:/Projects/OSRS-PS-DEV/rsmod/install_log4.txt'
    missing_obj, missing_seq = extract_missing_by_type(log_file)
    
    obj_sym = 'Z:/Projects/OSRS-PS-DEV/rsmod/.data/symbols/obj.sym'
    obj_out = 'Z:/Projects/OSRS-PS-DEV/rsmod/.data/symbols/.local/obj.sym'
    obj_maps = {
        'small_fishing_net': 'net',
        'big_fishing_net': 'big_net',
        'vial_of_water': 'vial_water',
        'vial': 'vial_empty',
        'raw_shrimps': 'raw_shrimp',
    }
    map_symbols(missing_obj, obj_sym, obj_out, obj_maps)
    
    seq_sym = 'Z:/Projects/OSRS-PS-DEV/rsmod/.data/symbols/seq.sym'
    seq_out = 'Z:/Projects/OSRS-PS-DEV/rsmod/.data/symbols/.local/seq.sym'
    seq_maps = {
        'human_firemaking': 'firemaking',
        'human_thieving_stall': 'human_pickpocket',
        'human_fishing_net': 'human_fishing_net',
    }
    map_symbols(missing_seq, seq_sym, seq_out, seq_maps)
