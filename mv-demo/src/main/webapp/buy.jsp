<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<%
    if (session.getAttribute("loggedUser") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
    
    String productId = request.getParameter("productId");
    
    Class.forName("org.h2.Driver");
    Connection conn = DriverManager.getConnection("jdbc:h2:mem:mvdb;DB_CLOSE_DELAY=-1", "sa", "");
    conn.setAutoCommit(false); // 开启事务
    
    try {
        // 1. 查询库存是否充足 (FOR UPDATE 悲观锁防超卖)
        PreparedStatement checkStmt = conn.prepareStatement("SELECT stock FROM products WHERE id = ? FOR UPDATE");
        checkStmt.setString(1, productId);
        ResultSet rs = checkStmt.executeQuery();
        
        if(rs.next() && rs.getInt("stock") > 0) {
            // 2. 扣减库存 (直接操作数据库，没有 Service 层)
            PreparedStatement updateStmt = conn.prepareStatement("UPDATE products SET stock = stock - 1 WHERE id = ?");
            updateStmt.setString(1, productId);
            updateStmt.executeUpdate();
            
            conn.commit();
            out.println("<script>alert('购买成功！库存已扣减。'); location.href='mall.jsp';</script>");
        } else {
            conn.rollback();
            out.println("<script>alert('抱歉，库存不足，购买失败！'); location.href='mall.jsp';</script>");
        }
    } catch(Exception e) {
        conn.rollback();
        out.println("<script>alert('系统错误'); location.href='mall.jsp';</script>");
    } finally {
        conn.close();
    }
%>
