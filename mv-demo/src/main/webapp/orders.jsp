<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<%
    if (session.getAttribute("loggedUser") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
    String username = (String) session.getAttribute("loggedUser");
%>
<html>
<head>
    <title>我的账单 - MV架构商城</title>
    <style>
        body { font-family: sans-serif; padding: 20px; background: #f4f4f4; }
        .header { background: white; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
        .card { background: white; padding: 15px; border-radius: 5px; margin-bottom: 10px; display: flex; justify-content: space-between; align-items: center; }
        .btn { background: #1890ff; color: white; border: none; padding: 8px 15px; cursor: pointer; border-radius: 3px; text-decoration: none; font-size: 14px; }
    </style>
</head>
<body>
    <div class="header">
        <h2>我的账单 | <a href="mall.jsp">返回商城</a></h2>
    </div>
    
    <div>
        <%
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:mem:mvdb;DB_CLOSE_DELAY=-1", "sa", "");
            try {
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM orders WHERE username = ? ORDER BY created_at DESC");
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                boolean hasOrders = false;
                
                while(rs.next()) {
                    hasOrders = true;
        %>
            <div class="card">
                <div>
                    <strong>订单号: <%= rs.getInt("id") %></strong> - 
                    <span><%= rs.getString("product_name") %></span>
                    <p style="margin: 5px 0 0; color: #888; font-size: 13px;"><%= rs.getTimestamp("created_at") %></p>
                </div>
                <div>
                    <span style="color:red; font-weight:bold; margin-right: 15px;">¥<%= rs.getBigDecimal("price") %></span>
                    <a href="bill.jsp?orderId=<%= rs.getInt("id") %>" class="btn">查看电子账单</a>
                </div>
            </div>
        <%
                }
                if (!hasOrders) {
                    out.println("<p>暂无账单记录。</p>");
                }
            } catch(Exception e) {
                out.println("查询失败：" + e.getMessage());
            } finally {
                conn.close();
            }
        %>
    </div>
</body>
</html>
