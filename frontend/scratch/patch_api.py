import re

def update_api_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    lines = content.split('\n')
    new_lines = []
    for line in lines:
        if '=> http.get(' in line and 'params:' not in line and 'dashboard' not in line and 'operation-board' not in line and 'reports' not in line and 'high-concurrency' not in line:
            m = re.match(r'^(export const \w+) = \(\) => http\.get\((.*?)\)$', line)
            if m:
                new_line = f"{m.group(1)} = (page = 0, size = 10) => http.get({m.group(2)}, {{ params: {{ page, size }} }})"
                new_lines.append(new_line)
                continue
        
        if 'getAdminOrders' in line and 'size = 50' in line:
            new_lines.append(line.replace('size = 50', 'size = 10'))
            continue
        if 'getAdminProducts' in line and 'size = 50' in line:
            new_lines.append(line.replace('size = 50', 'size = 10'))
            continue
        
        new_lines.append(line)
        
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write('\n'.join(new_lines))

update_api_file(r'e:\Programming\Code\MallSystem\frontend\src\api\admin.js')
update_api_file(r'e:\Programming\Code\MallSystem\frontend\src\api\mall.js')
