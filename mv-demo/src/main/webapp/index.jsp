<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<%
    // 初始化数据库表结构和测试数据 (H2内存数据库)
    try {
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:mvdb;DB_CLOSE_DELAY=-1", "sa", "");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(50), password VARCHAR(50))");
        stmt.execute("CREATE TABLE IF NOT EXISTS products (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50), price DECIMAL(10,2), stock INT)");
        
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
        rs.next();
        if(rs.getInt(1) == 0) {
            stmt.execute("INSERT INTO users (username, password) VALUES ('admin', '123456')");
            stmt.execute("INSERT INTO products (name, price, stock) VALUES ('MacBook Pro 16', 18999.00, 5)");
            stmt.execute("INSERT INTO products (name, price, stock) VALUES ('iPhone 15 Pro', 8999.00, 10)");
        }
        conn.close();
    } catch(Exception e) {}
%>
<html>
<head>
    <title>登录 - 极简MV架构演示</title>
    <style>body { font-family: sans-serif; padding: 50px; }</style>
</head>
<body>
    <h2>用户登录 (JSP直接连数据库)</h2>
    <form action="login.jsp" method="post">
        用户名: <input type="text" name="username" value="admin"><br><br>
        密 码: &nbsp;<input type="password" name="password" value="123456"><br><br>
        <input type="submit" value="登录">
    </form>
</body>
</html>
