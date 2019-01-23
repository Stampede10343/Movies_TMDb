cd Movies
chmod +x gradlew
./gradlew clean test jacocoTestReport assemble -PAPI_KEY='""'
