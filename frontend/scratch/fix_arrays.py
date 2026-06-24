import os

filepaths = [
    r'e:\Programming\Code\MallSystem\frontend\src\views\AdminView.vue',
    r'e:\Programming\Code\MallSystem\frontend\src\views\PlatformView.vue'
]

for filepath in filepaths:
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Revert analytics initialization
    content = content.replace("const analytics = ref({ content: [], page: 0, totalPages: 0 })", "const analytics = ref([])")
    content = content.replace("analytics.value = analyticsRes.data || { content: [], page: 0, totalPages: 0 }", "analytics.value = analyticsRes.data || []")
    content = content.replace("analytics.value = res.data || { content: [], page: 0, totalPages: 0 }", "analytics.value = res.data || []")
    
    # Revert template
    content = content.replace('v-for="item in analytics.content"', 'v-for="item in analytics"')
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

# Fix ProductView.vue
product_filepath = r'e:\Programming\Code\MallSystem\frontend\src\views\ProductView.vue'
with open(product_filepath, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace("const reviews = ref({ content: [], page: 0, totalPages: 0 })", "const reviews = ref([])")
content = content.replace("const questions = ref({ content: [], page: 0, totalPages: 0 })", "const questions = ref([])")
content = content.replace("reviews.value = reviewRes.data || { content: [], page: 0, totalPages: 0 }", "reviews.value = reviewRes.data || []")
content = content.replace("questions.value = questionRes.data || { content: [], page: 0, totalPages: 0 }", "questions.value = questionRes.data || []")

content = content.replace('v-for="review in reviews.content"', 'v-for="review in reviews"')
content = content.replace('v-for="question in questions.content"', 'v-for="question in questions"')
content = content.replace('!reviews.content.length', '!reviews.length')
content = content.replace('!questions.content.length', '!questions.length')
content = content.replace('{{ reviews.content.length }}', '{{ reviews.length }}')
content = content.replace('{{ questions.content.length }}', '{{ questions.length }}')

with open(product_filepath, 'w', encoding='utf-8') as f:
    f.write(content)
