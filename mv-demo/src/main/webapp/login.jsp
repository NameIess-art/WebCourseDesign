<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<%
    String user = request.getParameter("username");
    String pass = request.getParameter("password");
    
    Class.forName("org.h2.Driver");
    Connection conn = DriverManager.getConnection("jdbc:h2:mem:mvdb;DB_CLOSE_DELAY=-1", "sa", "");
    PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
    pstmt.setString(1, user);
    pstmt.setString(2, pass);
    ResultSet rs = pstmt.executeQuery();
    
    if (rs.next()) {
        session.setAttribute("loggedUser", user);
        response.sendRedirect("mall.jsp");
    } else {
        out.println("<script>alert('用户名或密码错误'); history.back();</script>");
    }
    conn.close();
%>
