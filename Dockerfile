# Sử dụng JDK 17
FROM eclipse-temurin:17-jdk

# Đặt thư mục làm việc
WORKDIR /app

ARG JAR_FILE=target/*.jar

# Copy file JAR vào container
COPY ${JAR_FILE} app.jar

# Mở cổng 8080
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
