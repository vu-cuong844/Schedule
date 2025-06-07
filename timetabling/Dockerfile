FROM eclipse-temurin:17-jdk

RUN apt-get update && apt-get install -y \
    libstdc++6 libgomp1 zlib1g unzip && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy jar ứng dụng (sau khi bạn đã build bằng mvn clean package)
COPY target/timetabling-0.0.1-SNAPSHOT.jar app.jar

# Copy thư viện native vào thư mục /usr/lib/ortools trong container
COPY libs/libjniortools.so /usr/lib/ortools/

# Thiết lập biến môi trường để Java biết tìm thư viện native ở đó
ENV LD_LIBRARY_PATH=/usr/lib/ortools
ENV JAVA_LIBRARY_PATH=/usr/lib/ortools

EXPOSE 8082

ENTRYPOINT ["java", "-Xmx1024m", "-Djava.library.path=/usr/lib/ortools", "-jar", "app.jar"]
