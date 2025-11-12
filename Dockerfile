# Giai đoạn 1: Build (Sử dụng Maven 3 và Eclipse Temurin (OpenJDK) 21)
# Image này đã bao gồm cả Maven và JDK 21
FROM maven:3.9.8-eclipse-temurin-21 AS build

# Tạo thư mục làm việc
WORKDIR /app

# Sao chép tệp cấu hình Maven
COPY pom.xml .

# Sao chép mã nguồn
COPY src ./src

# Biên dịch và đóng gói ứng dụng thành JAR
# Sử dụng cờ -DskipTests để bỏ qua chạy test (giúp build nhanh hơn)
RUN mvn clean package -DskipTests

# Đổi tên JAR file (sử dụng wildcard để lấy file .jar duy nhất trong target)
ARG JAR_FILE=target/*.jar
RUN mv ${JAR_FILE} app.jar


# Giai đoạn 2: Runtime (Sử dụng JRE 21 TỐI ƯU)
# Chúng ta dùng "eclipse-temurin:21-jre" thay vì "alpine"
# LÝ DO: Dự án của bạn có "onnxruntime" (thư viện Machine Learning).
# Đây là thư viện native, nó sẽ chạy an toàn hơn trên JRE bản đầy đủ (glibc)
# thay vì bản Alpine (musl).
FROM eclipse-temurin:21-jre

# Tạo thư mục làm việc
WORKDIR /app

# Sao chép JAR đã được build từ giai đoạn 1
COPY --from=build /app/app.jar .

EXPOSE 3000

# Lệnh chạy ứng dụng
CMD ["java", "-jar", "app.jar"]