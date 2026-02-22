import re
import os

def load_missing(log_file):
    missing = []
    with open(log_file, 'r', encoding='utf-8') as f:
        in_list = False
        for line in f:
            if 'The following references use names that are not defined in a .sym file' in line:
                in_list = True
                continue
            if in_list:
                m = re.search(r'- Name: "(.*?)"', line)
                if m:
                    missing.append(m.group(1))
                elif 'at ' in line or line.strip() == '':
                    in_list = False
    return list(set(missing))

def categorize(name):
    objs = [
        'potion', 'unf', 'herb', 'vial', 'eggs', 'legs', 'fungus', 'cactus', 'nest', 'ground', 'brew', 'shrimps', 
        'anchovies', 'sardine', 'mackerel', 'shrimpcage', 'harpoon', 'net', 'bait', 'compass', 'knife', 'chisel', 
        'hammer', 'feather'
    ]
    locs = ['stall', 'chest', 'null_', 'cabinet', 'door', 'gate', 'tree', 'rock', 'vein']
    npcs = ['goblin', 'chicken', 'rat', 'guard', 'farmer', 'thug', 'member', 'lurefish', 'bignet', 'monkfish', 'barb', 'darkcrab', 'anglerfish']
    params = ['damage', 'ticks', 'toxin', 'immunity', 'sub_tick']
    
    name_lower = name.lower()
    
    for word in locs:
        if word in name_lower: return 'loc'
    for word in npcs:
        if word in name_lower: return 'npc'
    for word in objs:
        if word in name_lower: return 'obj'
    for word in params:
        if word in name_lower: return 'param'
        
    return 'obj' # default

def distribute(missing):
    mappings = {'obj': [], 'npc': [], 'loc': [], 'param': [], 'seq': []}
    
    # We also have some manual overrides from previous sessions
    manual = {
        'toad_legs': 'obj',
        'swamp_toad_legs': 'obj',
        'raw_shrimps': 'obj',
        'raw_anchovies': 'obj',
        'raw_sardine': 'obj',
        'raw_mackerel': 'obj',
        'human_smithing': 'seq',
        'human_fletching_knife': 'seq',
        'human_herblore': 'seq'
    }

    for name in missing:
        t = manual.get(name, categorize(name))
        mappings[t].append(name)
        
    return mappings

def write_local_syms(mappings):
    base_dir = r"Z:\Projects\OSRS-PS-DEV\rsmod\.data\symbols\.local"
    if not os.path.exists(base_dir):
        os.makedirs(base_dir)
        
    for t, names in mappings.items():
        if not names: continue
        file_path = os.path.join(base_dir, f"{t}.sym")
        
        # We assign high IDs to avoid any potential overlap with vanilla IDs, 
        # but within valid cache ranges (typically 0-30000 for Objs, 0-10000 for NPCs/Locs).
        # To be safe, we'll start at 1 and increment. The server allows duplicate IDs 
        # as long as the names are unique, but it's better to stay safe.
        # Actually, let's use 10000+ for Objs and NPCs, Locs etc.
        
        start_id = 20000 if t == 'obj' else 10000
        out_lines = []
        for i, name in enumerate(sorted(names)):
            out_lines.append(f"{start_id + i}\t{name}")
            
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(out_lines))
        print(f"Wrote {len(names)} entries to {t}.sym")

if __name__ == '__main__':
    missing = load_missing(r"Z:\Projects\OSRS-PS-DEV\rsmod\run_log.txt")
    print(f"Found {len(missing)} unique missing symbols.")
    mappings = distribute(missing)
    write_local_syms(mappings)
