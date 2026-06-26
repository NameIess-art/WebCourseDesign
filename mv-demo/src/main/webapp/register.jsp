<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<%
    request.setCharacterEncoding("UTF-8");
    String user = request.getParameter("username");
    String pass = request.getParameter("password");
    
    if (user == null || user.trim().isEmpty() || pass == null || pass.trim().isEmpty()) {
        out.println("<script>alert('用户名或密码不能为空'); history.back();</script>");
        return;
    }

    Class.forName("org.h2.Driver");
    Connection conn = DriverManager.getConnection("jdbc:h2:mem:mvdb;DB_CLOSE_DELAY=-1", "sa", "");
    
    try {
        // 检查用户名是否已存在
        PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
        checkStmt.setString(1, user);
        ResultSet rs = checkStmt.executeQuery();
        
        if (rs.next()) {
            out.println("<script>alert('用户名已存在，请换一个'); history.back();</script>");
        } else {
            // 插入新用户
            PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            insertStmt.setString(1, user);
            insertStmt.setString(2, pass);
            insertStmt.executeUpdate();
            
            out.println("<script>alert('注册成功，请登录！'); location.href='index.jsp';</script>");
        }
    } catch(Exception e) {
        out.println("<script>alert('系统错误：" + e.getMessage() + "'); history.back();</script>");
    } finally {
        conn.close();
    }
%>
