<h1>Ohjelmistotekniikka - Minesweeper</h1>

Sovellus on yhden pelaajan pelattava miinaharava peli.

<h3>Dokumentaatio</h3>

<a href="https://github.com/Jimmeeee/ot-harjoitustyo/blob/master/Minesweeper/dokumentaatio/vaativuusmaarittely.md">Vaativuusmäärittely</a>

<a href="https://github.com/Jimmeeee/ot-harjoitustyo/blob/master/Minesweeper/dokumentaatio/tyoaikakirjanpito.md">Työaikakirjanpito</a>

<a href="https://github.com/Jimmeeee/ot-harjoitustyo/blob/master/Minesweeper/dokumentaatio/arkkitehtuuri.md">Arkkitehtuuri</a>

<a href="https://github.com/Jimmeeee/ot-harjoitustyo/blob/master/Minesweeper/dokumentaatio/testausdokumentti.md">Testausdokumentti</a>

<a href="https://github.com/Jimmeeee/ot-harjoitustyo/blob/master/Minesweeper/dokumentaatio/kayttoohje.md">Käyttöohje</a>

<h3>Releaset</h3>
<a href="https://github.com/Jimmeeee/ot-harjoitustyo/releases/tag/Viikko5">Viikko 5 release</a>

<a href="https://github.com/Jimmeeee/ot-harjoitustyo/releases/tag/Viikko6">Viikko 6 release</a>

<a href="https://github.com/Jimmeeee/ot-harjoitustyo/releases/tag/Viikko7">Loppupalautus</a>


<h3>Komentorivitoiminnot</h3>
<h4>Testaus</h4>

Testit suoritetaan komennolla

<code>mvn test</code>

Testikattavuusraportti luokaan komennolla

<code>mvn jacoco:report</code>

Kattavuusraporttia voi tarkastella avaamalla selaimella tiedosto _target/site/jacoco/index.html_

<h4>Suoritettavan jarin generointi</h4>

<code>mvn package</code>

Tämä generoi hakemistoon _target_ suoritettavan jar-tiedoston.

<h4>Checkstyle</h4>

<code>mvn jxr:jxr checkstyle:checkstyle</code>

Raportin selviää avaamalla selaimella tiedosto _target/site/checkstyle.html_

