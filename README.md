# IDATT2105_Fullstack_Project
Project in the course IDATT2105 where the assignment were to create a room reservation webappliaction. 


## idatt2105-project-BE
Project in the course IDATT2105 backend developed with Spring Boot.

### Project setup

Open application.properties in the resource folder under idatt2105-project-BE/src/main
and edit spring.datasource.url to own database url as well as
spring.datasource username and password fields to personal database username and password

### CORS Config
If the client doesnt use localhost:8080, add the desired URL to CorsConfig.java in the config folder.
Add the URL to the list found in the method 'config.setAllowedOrigins(List.of("http://localhost:8080"));'. 

### API Documentation with Swagger UI
http://localhost:8081/swagger-ui.html

### Third party libraries used

#### JWT implementation by teacher Alexander Holt
https://gitlab.stud.idi.ntnu.no/alexholt/spring-boot-aop

#### JaCoCO
https://www.jacoco.org/jacoco/trunk/index.html

#### JJWT 
https://java.jsonwebtoken.io/

## idatt2105-project-FE
Project in the course IDATT2105 frontend developed with Vue3

PS: When accessing the site for the first time you have to use one of the predefined accounts
(admin@gmail.com password: admin or user@gmail.com password: user) to log in. To create your own user you have to log in to the admin site
(url: localhost:8080/login/admin) with the admin user and create your own user with your mail (in order to recieve password).

### Project setup
```
npm install
```

#### Compiles and hot-reloads for development
```
npm run serve
```

#### Compiles and minifies for production
```
npm run build
```

#### Lints and fixes files
```
npm run lint
```
#### Set the URI for the server
To specify a URI other than localhost:8081, use the .env
and set the 'VUE_APP_API_URL' variable to the wanted URI.


#### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).

### Third party libraries used

#### Mosha Vue Toastify
https://github.com/szboynono/mosha-vue-toastify#the-gist

#### JWT implementation by teacher Alexander Holt
https://gitlab.stud.idi.ntnu.no/alexholt/vue3-auth

#### Frappe Charts
https://frappe.io/charts

#### Moment.js
https://momentjs.com/

#### Loading.io
https://loading.io/css

#### Axios 
https://axios-http.com/docs/intro
