<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<%
    if (session.getAttribute("loggedUser") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>
<html>
<head>
    <title>MV 商城首页</title>
    <style>
        body { font-family: sans-serif; background: #f0f2f5; margin: 0; padding: 20px; }
        .header { display: flex; justify-content: space-between; align-items: center; background: white; padding: 15px 30px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); margin-bottom: 20px; }
        .product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 20px; }
        .product-card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); text-align: center; }
        .price { color: #f5222d; font-size: 1.2em; font-weight: bold; margin: 10px 0; }
        .buy-btn { background: #52c41a; color: white; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
    </style>
</head>
<body>
    <div class="header">
        <h2>MV 架构商城</h2>
        <div>
            <span>欢迎, <%= session.getAttribute("loggedUser") %></span>
            <a href="logout.jsp" style="margin-left: 15px; color: #1890ff; text-decoration: none;">退出登录</a>
        </div>
    </div>

    <div class="product-grid">
        <% 
            try {
                Class.forName("org.h2.Driver");
                Connection conn = DriverManager.getConnection("jdbc:h2:mem:mvdb;DB_CLOSE_DELAY=-1", "sa", "");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM products");
                
                while (rs.next()) {
        %>
            <div class="product-card">
                <h3><%= rs.getString("name") %></h3>
                <div class="price">￥<%= rs.getBigDecimal("price") %></div>
                <button class="buy-btn" onclick="alert('购买成功！')">购买</button>
            </div>
        <%
                }
                conn.close();
            } catch (Exception e) {
                out.println("<p>加载商品失败: " + e.getMessage() + "</p>");
            }
        %>
    </div>
</body>
</html>
