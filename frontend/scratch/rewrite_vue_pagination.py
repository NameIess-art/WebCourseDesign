import os
import re

views_dir = r'e:\Programming\Code\MallSystem\frontend\src\views'

def process_vue_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Find arrays
    refs = re.findall(r'const (\w+) = ref\(\{ content: \[\], page: 0, totalPages: 0 \}\)', content)
    
    for r in refs:
        # We need to find where the array is rendered.
        # usually <article v-for="item in r.content"...> </article>
        # We find the closing tag of the parent container or the article itself.
        
        # A simpler way: just append <Pagination :page="r.page" :totalPages="r.totalPages" @change="loadData" />
        # Wait! We need a specific load method for each list.
        # Let's generate loadXXXPage methods.
        pass

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

for f in os.listdir(views_dir):
    if f.endswith('.vue'):
        process_vue_file(os.path.join(views_dir, f))
