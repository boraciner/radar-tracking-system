# radar-tracking-system
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/boraciner
[![LinkedIn][linkedin-shield]][linkedin-url]



<!-- PROJECT LOGO -->
<br />
<p align="center">
    <img src="assets/sys.jpeg" alt="Logo">
    <img src="https://cdn.pixabay.com/animation/2022/11/13/04/07/04-07-28-574_512.gif" alt="Logo">
    <h3 align="center">Microservices in radar track extraction</h3>
<p>
    Spring Boot Applications
    <br />
-   radar-service: generating 2D plot data for 5 tracks with respect to 2nd degree of polynomial function<br />
-   plot-listener-service: listens plots from any source and writes to Msql DB<br />
Endpoint:<br />
POST /tracks (port : 8100)<br />
-   tracker-service: retrieves the plot data and generates tracks by running kalman or other tracker filters<br />

  </p>
</p>
