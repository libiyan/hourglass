FROM registry.cn-shenzhen.aliyuncs.com/drgou-test/centos:7.4.1
RUN /bin/cp  /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
ADD target/*.jar /app.jar

EXPOSE  8097

#ENV JAVA_OPTS="-Dspring.profiles.active=dev -Djava.net.preferIPv4Stack=true -Dspring.cloud.nacos.discovery.namespace=98b242ec-2448-4836-9953-b69e18e025db -Dspring.cloud.nacos.config.namespace=98b242ec-2448-4836-9953-b69e18e025db  -Dspring.cloud.nacos.discovery.server-addr=172.18.66.90:8848 -Dspring.cloud.nacos.config.server-addr=172.18.66.90:8848 -Dlogging.path=/data/logs/app_logs -Dlogging.level.root=info -server -Xms512m -Xmx512m -Xmn200m -Xss256k -XX:+DisableExplicitGC -XX:+UseG1GC -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -Djava.security.egd=file:/dev/urandom -XX:ErrorFile=/var/app/gc/hs_err_pid%p.log  -XX:HeapDumpPath=/var/app/gc  -Xloggc:/var/app/gc/gc%t.log"

ENTRYPOINT [ "sh", "-c","java $JAVA_OPTS -jar /app.jar" ]