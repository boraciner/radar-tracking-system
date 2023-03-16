# radar-tracking-system
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/boraciner
[![LinkedIn][linkedin-shield]][linkedin-url]



<!-- PROJECT LOGO -->
<br />
<p align="center">
    <img src="sys.jpeg" alt="Logo">
    <img src="https://cdn.pixabay.com/animation/2022/11/13/04/07/04-07-28-574_512.gif" alt="Logo" width="300" height="300">
    <h3 align="center">Microservices in radar track extraction</h3>
<p>
    Spring Boot Applications
    <br />
-   naming-service (8761): Eureka Server
-   radar-service (8000): generating 2D plot data for 5 tracks with respect to 2nd degree of polynomial function<br />
-   plot-listener-service (8100): listens plots from any source and publishes to Kafka topic : radar-plot cluster<br />
Endpoint:<br />
POST /tracks<br />
-   tracker-service (8200): listens plot data from radar-plot kafka topic and publishes tracks to radar-track topic by running kalman or other tracker filters<br />
-   map-viewer-service (8080) : listens the track data from radar-track topic and displays them in a radar scope by using Websocket to update them dynamically.
  </p>
</p>
