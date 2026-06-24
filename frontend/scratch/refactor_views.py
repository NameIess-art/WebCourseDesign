import os
import re

views_dir = r'e:\Programming\Code\MallSystem\frontend\src\views'

def patch_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Find all refs initialized with []
    # e.g., const products = ref([])
    refs = re.findall(r'const (\w+) = ref\(\[\]\)', content)
    
    # We will replace them with const products = ref({ content: [], page: 0, totalPages: 0 })
    for r in refs:
        content = re.sub(fr'const {r} = ref\(\[\]\)', f'const {r} = ref({{ content: [], page: 0, totalPages: 0 }})', content)
        
        # In script, assignments like products.value = res.data || []
        # change to products.value = res.data || { content: [], page: 0, totalPages: 0 }
        # Need to be careful. A common pattern is `xxx.value = yyy.data || []`
        content = re.sub(fr'{r}\.value = (.*?)\.data \|\| \[\]', fr'{r}.value = \1.data || {{ content: [], page: 0, totalPages: 0 }}', content)

        # Some might be `xxx.value = yyy || []`
        content = re.sub(fr'{r}\.value = ([\w\.\(\)]+) \|\| \[\]', fr'{r}.value = \1 || {{ content: [], page: 0, totalPages: 0 }}', content)

        # In template, v-for="item in products" -> v-for="item in products.content"
        # We find v-for=".*? in {r}"
        content = re.sub(fr'v-for="(.*?)\s+in\s+{r}"', fr'v-for="\1 in {r}.content"', content)

        # If there are lengths: products.length -> products.content.length
        content = re.sub(fr'{r}\.length', f'{r}.content.length', content)
        
        # If there are array methods: products.value.filter -> products.value.content.filter
        content = re.sub(fr'{r}\.value\.filter', f'{r}.value.content.filter', content)
        content = re.sub(fr'{r}\.value\.map', f'{r}.value.content.map', content)
        content = re.sub(fr'{r}\.value\.push', f'{r}.value.content.push', content)
        content = re.sub(fr'{r}\.value\.unshift', f'{r}.value.content.unshift', content)
        content = re.sub(fr'{r}\.value\.splice', f'{r}.value.content.splice', content)

    # Now we need to append the Pagination component at the end of the lists.
    # We can inject Pagination import
    if 'import Pagination from' not in content:
        content = content.replace("from 'vue'", "from 'vue'\nimport Pagination from '../components/Pagination.vue'")

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

for f in os.listdir(views_dir):
    if f.endswith('.vue'):
        patch_file(os.path.join(views_dir, f))
