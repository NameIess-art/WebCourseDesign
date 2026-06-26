<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<%
    if (session.getAttribute("loggedUser") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
    
    String orderId = request.getParameter("orderId");
    String username = (String) session.getAttribute("loggedUser");
    
    Class.forName("org.h2.Driver");
    Connection conn = DriverManager.getConnection("jdbc:h2:mem:mvdb;DB_CLOSE_DELAY=-1", "sa", "");
    
    String productName = "";
    java.math.BigDecimal price = null;
    Timestamp createdAt = null;
    
    try {
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM orders WHERE id = ? AND username = ?");
        pstmt.setString(1, orderId);
        pstmt.setString(2, username);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            productName = rs.getString("product_name");
            price = rs.getBigDecimal("price");
            createdAt = rs.getTimestamp("created_at");
        } else {
            out.println("<script>alert('账单不存在或无权查看！'); location.href='mall.jsp';</script>");
            return;
        }
    } catch(Exception e) {
        out.println("查询账单失败：" + e.getMessage());
        return;
    } finally {
        conn.close();
    }
%>
<html>
<head>
    <title>电子账单</title>
    <style>
        body { font-family: monospace, sans-serif; padding: 20px; background: #f4f4f4; display: flex; justify-content: center; }
        .bill-card { background: white; width: 350px; padding: 30px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
        .bill-header { text-align: center; border-bottom: 2px dashed #ccc; padding-bottom: 15px; margin-bottom: 15px; }
        .bill-header h2 { margin: 0; color: #333; }
        .subtitle { color: #888; font-size: 14px; margin-top: 5px; }
        .info-row { display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px; }
        .total-row { display: flex; justify-content: space-between; margin-top: 20px; padding-top: 15px; border-top: 2px dashed #ccc; font-size: 18px; font-weight: bold; }
        .actions { margin-top: 30px; display: flex; justify-content: space-between; }
        .btn { padding: 8px 15px; text-decoration: none; border-radius: 4px; cursor: pointer; border: none; font-size: 14px; }
        .btn-primary { background: #1890ff; color: white; }
        .btn-default { background: #eee; color: #333; }
        @media print {
            body { background: white; }
            .bill-card { box-shadow: none; }
            .actions { display: none; }
        }
    </style>
</head>
<body>
    <div class="bill-card">
        <div class="bill-header">
            <h2>MV 商城消费凭证</h2>
            <div class="subtitle">电子账单</div>
        </div>
        
        <div class="info-row">
            <span>订单编号：</span>
            <span><%= orderId %></span>
        </div>
        <div class="info-row">
            <span>交易时间：</span>
            <span><%= createdAt %></span>
        </div>
        <div class="info-row">
            <span>购买账号：</span>
            <span><%= username %></span>
        </div>
        
        <br>
        
        <div class="info-row" style="font-weight: bold;">
            <span>商品明细</span>
            <span>金额</span>
        </div>
        <div class="info-row">
            <span><%= productName %></span>
            <span>¥<%= price %></span>
        </div>
        
        <div class="total-row">
            <span>实付总计：</span>
            <span>¥<%= price %></span>
        </div>
        
        <div class="actions">
            <a href="mall.jsp" class="btn btn-default">返回商城</a>
            <button onclick="window.print()" class="btn btn-primary">打印账单</button>
        </div>
    </div>
</body>
</html>
