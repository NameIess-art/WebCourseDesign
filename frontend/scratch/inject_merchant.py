import re

filepath = r'e:\Programming\Code\MallSystem\frontend\src\views\MerchantView.vue'
with open(filepath, 'r', encoding='utf-8') as f: content = f.read()

# Fix MerchantView.vue
content = re.sub(r'(</article>\s*</div>)\s*</section>\s*<section class="section-card">\s*<div class="section-head">\s*<h2>订单',
r'\1\n        <Pagination v-if="products.totalPages > 1" :page="products.page" :totalPages="products.totalPages" @change="p => getAdminProducts(p, 10).then(res => products = res.data)" />\n      </div>\n    </section>\n\n    <section class="section-card">\n      <div class="section-head">\n        <h2>订单', content)

content = re.sub(r'(</article>\s*</div>)\s*<div class="stack-list">\s*<h3>售后',
r'\1\n        <Pagination v-if="orders.totalPages > 1" :page="orders.page" :totalPages="orders.totalPages" @change="p => getAdminOrders(null, p, 10).then(res => orders = res.data)" />\n      </div>\n      <div class="stack-list">\n        <h3>售后', content)

content = re.sub(r'(</article>\s*</div>)\s*</section>\s*<section class="section-card">\s*<div class="section-head">\s*<h2>营销',
r'\1\n        <Pagination v-if="afterSales.totalPages > 1" :page="afterSales.page" :totalPages="afterSales.totalPages" @change="p => getAdminAfterSales(p, 10).then(res => afterSales = res.data)" />\n      </div>\n    </section>\n\n    <section class="section-card">\n      <div class="section-head">\n        <h2>营销', content)

content = re.sub(r'(</tr>\s*</tbody>\s*</table>)\s*</section>\s*<section class="section-card">\s*<div class="section-head">\s*<h2>店铺',
r'\1\n        <Pagination v-if="marketing.totalPages > 1" :page="marketing.page" :totalPages="marketing.totalPages" @change="p => getMarketing(p, 10).then(res => marketing = res.data)" />\n      </section>\n\n    <section class="section-card">\n      <div class="section-head">\n        <h2>店铺', content)

content = re.sub(r'(</article>\s*</div>)\s*</section>\s*</section>\s*</template>',
r'\1\n        <Pagination v-if="merchantsList.totalPages > 1" :page="merchantsList.page" :totalPages="merchantsList.totalPages" @change="p => getMerchants(p, 10).then(res => merchantsList = res.data)" />\n      </div>\n    </section>\n  </section>\n</template>', content)

with open(filepath, 'w', encoding='utf-8') as f: f.write(content)
