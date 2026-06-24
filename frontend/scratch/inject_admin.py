import re
import os

filepath = r'e:\Programming\Code\MallSystem\frontend\src\views\AdminView.vue'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

mappings = {
    'products': 'getAdminProducts(p, 10)',
    'orders': 'getAdminOrders(null, p, 10)',
    'marketing': 'getMarketing(p, 10)',
    'marketingFlows': 'getMarketingFlows(p, 10)',
    'configs': 'getConfigs(p, 10)',
    'risks': 'getRiskItems(p, 10)',
    'afterSales': 'getAdminAfterSales(p, 10)',
    'roles': 'getRoles(p, 10)',
    'merchants': 'getMerchants(p, 10)',
    'audits': 'getContentAudits(p, 10)'
}

for key, api_call in mappings.items():
    # Find the tag containing the v-for loop
    # We look for a pattern like <article v-for="var in key.content"...> ... </article>
    # Since regex over multi-line HTML can be fragile, we'll find `v-for="... in key.content"`
    # and insert Pagination BEFORE the next `</div>` or `</section>`
    # But wait! A better way is:
    # Just do a literal string replacement for known endings.
    pass

# We can just append the Pagination at the end of the enclosing div.
# Let's write a generic function that takes the file content, the key, and the API call,
# and inserts the Pagination component.
def inject_pagination(text, key, api_call):
    pattern = r'(v-for="\w+ in ' + key + r'\.content"[\s\S]*?</article>)\s*(</div>|</section>)'
    
    # Check if we match
    if re.search(pattern, text):
        replacement = r'\1\n        <Pagination v-if="' + key + r'.totalPages > 1" :page="' + key + r'.page" :totalPages="' + key + r'.totalPages" @change="p => ' + api_call + r'.then(res => ' + key + r' = res.data)" />\n      \2'
        text = re.sub(pattern, replacement, text, count=1)
    else:
        # try matching <p v-for ... </p>
        pattern2 = r'(v-for="\w+ in ' + key + r'\.content"[\s\S]*?</p>)\s*(</article>|</div>)'
        if re.search(pattern2, text):
            replacement2 = r'\1\n        <Pagination v-if="' + key + r'.totalPages > 1" :page="' + key + r'.page" :totalPages="' + key + r'.totalPages" @change="p => ' + api_call + r'.then(res => ' + key + r' = res.data)" />\n      \2'
            text = re.sub(pattern2, replacement2, text, count=1)
        else:
            print(f"Could not find injection point for {key}")
    return text

for key, api_call in mappings.items():
    content = inject_pagination(content, key, api_call)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
