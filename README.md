# gulimall
谷粒商城 

修改本地host文件 使域名解析为nginx服务器ip地址

    gulimall.com 127.0.0.1

nginx配置文件配置相应的映射规则

	upstream gulimall {
		server 127.0.0.1:88;
	}

    server {
        listen       80;
        server_name  gulimall.com;

		location / {
			proxy_set_header Host $host;
			proxy_pass http://gulimall;
		}
    }

nginx服务器监听本机的80端口 并匹配host为gulimall.com的请求，将通过负载均衡gulimall地址列表把该请求转发到对应服务器地址（网关服务地址，可以配置多个），并携带Header中Host的信息。

网关服务配置路由，匹配**.gulimall.com，将该路径全部转发到指定服务。