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
    <title>MV架构 商城</title>
    <style>
        body { font-family: sans-serif; padding: 20px; background: #f4f4f4; }
        .header { background: white; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
        .card { background: white; padding: 15px; border-radius: 5px; margin-bottom: 10px; display: inline-block; width: 200px; text-align: center; margin-right: 15px;}
        .buy-btn { background: #1890ff; color: white; border: none; padding: 8px 15px; cursor: pointer; border-radius: 3px; }
        .buy-btn:disabled { background: #ccc; cursor: not-allowed; }
    </style>
</head>
<body>
    <div class="header">
        <h2>欢迎, <%= session.getAttribute("loggedUser") %> | <a href="orders.jsp">我的账单</a> | <a href="logout.jsp">退出</a></h2>
    </div>
    
    <h3>商品列表</h3>
    <div>
        <%
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:mem:mvdb;DB_CLOSE_DELAY=-1", "sa", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM products");
            while(rs.next()) {
        %>
            <div class="card">
                <h4><%= rs.getString("name") %></h4>
                <p style="color:red; font-weight:bold;">￥<%= rs.getBigDecimal("price") %></p>
                <p>库存: <%= rs.getInt("stock") %></p>
                <form action="buy.jsp" method="post" style="margin:0;">
                    <input type="hidden" name="productId" value="<%= rs.getInt("id") %>">
                    <input type="submit" class="buy-btn" value="购买" <%= rs.getInt("stock") > 0 ? "" : "disabled" %>>
                </form>
            </div>
        <%
            }
            conn.close();
        %>
    </div>
</body>
</html>
