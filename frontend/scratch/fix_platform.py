import re
filepath = r'e:\Programming\Code\MallSystem\frontend\src\views\PlatformView.vue'
with open(filepath, 'r', encoding='utf-8') as f: content = f.read()

# Fix ListPanel items
content = content.replace(':items="risks"', ':items="risks.content"')
content = content.replace(':items="merchants"', ':items="merchants.content"')
content = content.replace(':items="contentAudits"', ':items="contentAudits.content"')
content = content.replace(':items="[...roles, ...permissions]"', ':items="[...(roles.content || []), ...(permissions.content || [])]"')

# Inject pagination for ListPanel
content = content.replace('</ListPanel>\n        <ListPanel title="商家处罚与启停"', 
'</ListPanel>\n        <Pagination v-if="risks.totalPages > 1" :page="risks.page" :totalPages="risks.totalPages" @change="p => getRiskItems(p, 10).then(res => risks = res.data)" />\n        <ListPanel title="商家处罚与启停"')

content = content.replace('</ListPanel>\n      </div>\n      <div class="dual-grid">\n        <ListPanel title="角色与权限"', 
'</ListPanel>\n        <Pagination v-if="merchants.totalPages > 1" :page="merchants.page" :totalPages="merchants.totalPages" @change="p => getMerchants(p, 10).then(res => merchants = res.data)" />\n      </div>\n      <div class="dual-grid">\n        <ListPanel title="角色与权限"')

# For roles/permissions, skip pagination or add it for roles? Skip for now since it's combined array.

content = content.replace('</ListPanel>\n      </div>\n    </section>\n\n    <section id="configs"', 
'</ListPanel>\n        <Pagination v-if="contentAudits.totalPages > 1" :page="contentAudits.page" :totalPages="contentAudits.totalPages" @change="p => getContentAudits(p, 10).then(res => contentAudits = res.data)" />\n      </div>\n    </section>\n\n    <section id="configs"')

# For marketing
content = re.sub(r'(</article>\s*</div>)\s*</section>\s*<section id="risk"',
r'\1\n      <Pagination v-if="marketing.totalPages > 1" :page="marketing.page" :totalPages="marketing.totalPages" @change="p => getMarketing(p, 10).then(res => marketing = res.data)" />\n    </section>\n\n    <section id="risk"', content)

# For configs
content = re.sub(r'(</tr>\s*</tbody>\s*</table>)\s*</section>\s*<section id="dictionaries"',
r'\1\n        <Pagination v-if="configs.totalPages > 1" :page="configs.page" :totalPages="configs.totalPages" @change="p => getConfigs(p, 10).then(res => configs = res.data)" />\n      </section>\n\n    <section id="dictionaries"', content)

# For dictionaries
content = re.sub(r'(</tr>\s*</tbody>\s*</table>)\s*</section>\s*<section id="promotion"',
r'\1\n        <Pagination v-if="dictionaries.totalPages > 1" :page="dictionaries.page" :totalPages="dictionaries.totalPages" @change="p => getDictionaries(p, 10).then(res => dictionaries = res.data)" />\n      </section>\n\n    <section id="promotion"', content)

# For announcements
content = re.sub(r'(</tr>\s*</tbody>\s*</table>)\s*</div>\n    </section>\n  </div>\n</template>',
r'\1\n        <Pagination v-if="announcements.totalPages > 1" :page="announcements.page" :totalPages="announcements.totalPages" @change="p => getAnnouncements(p, 10).then(res => announcements = res.data)" />\n      </div>\n    </section>\n  </div>\n</template>', content)

with open(filepath, 'w', encoding='utf-8') as f: f.write(content)
