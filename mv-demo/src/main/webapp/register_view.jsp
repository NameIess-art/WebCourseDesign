<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>注册账号 - 极简MV架构演示</title>
    <style>
        body { font-family: sans-serif; padding: 50px; background: #f4f4f4; }
        .card { background: white; padding: 30px; border-radius: 5px; width: 300px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        input[type="text"], input[type="password"] { width: 100%; padding: 8px; margin: 10px 0; box-sizing: border-box; }
        .btn { background: #1890ff; color: white; border: none; padding: 10px; width: 100%; cursor: pointer; border-radius: 3px; font-size: 16px; }
        .btn:hover { background: #096dd9; }
        a { color: #1890ff; text-decoration: none; font-size: 14px; }
    </style>
</head>
<body>
    <div class="card">
        <h2>用户注册</h2>
        <form action="register.jsp" method="post">
            <label>用户名：</label>
            <input type="text" name="username" required>
            
            <label>密码：</label>
            <input type="password" name="password" required>
            
            <input type="submit" class="btn" value="立即注册">
        </form>
        <div style="text-align: center; margin-top: 15px;">
            <a href="index.jsp">返回登录</a>
        </div>
    </div>
</body>
</html>
