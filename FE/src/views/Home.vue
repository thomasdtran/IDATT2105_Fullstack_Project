<template>
  <div class="tabset">
    <!-- Tab 1 -->
    <input type="radio" name="tabset" id="tab1" aria-controls="room" checked>
    <label for="tab1">Søk</label>
    <!-- Tab 2 -->
    <input type="radio" name="tabset" id="tab2" aria-controls="sections">
    <label for="tab2" @click="reloadRes">Mine reservasjoner</label>
    <!-- Tab 3 -->
    <input type="radio" name="tabset" id="tab3" aria-controls="stats">
    <label for="tab3">Statistikk</label>
    <!-- Tab 4 -->
    <input type="radio" name="tabset" id="tab4" aria-controls="kontakt">
    <label for="tab4">Kontakt</label>

    <button class="btn" id="logout" @click="logout">Logg ut</button>

    <div class="tab-panels">
      <section id="room" class="tab-panel">
        <h2>Søk etter rom å reservere</h2>
        <SearchField/>
      </section>

      <section id="sections" class="tab-panel">
        <h2 id="sectionHeader">Dine reservasjoner</h2>
        <my-reservations :reload="reload"/>
      </section>

      <section id="stats" class="tab-panel">
        <room-stats></room-stats>
      </section>

      <section id="kontakt" class="tab-panel">
        <div id="aboutus">
          <b>Kontakt</b>
          <pre>{{aboutinfo}}</pre>
        </div>
      </section>
    </div>

  </div>


</template>

<script>
import SearchField from "@/components/SearchField.vue"
import RoomStats from "@/components/RoomStats.vue"
import MyReservations from "@/components/MyReservations.vue"
import store from "@/store/index";

export default {
  data(){
    return{
      reload: false,
      aboutinfo: 
      "Funnet bugs?\nØnsker du endringer på brukeren din?\nKontakt oss så vi kan fikse det :) \n\ne-post: roomiu@zectionet.com \ntlf: +47 474 84 950 \n"
    }
  },
  components:{
    SearchField,
    RoomStats,
    MyReservations
  },
  methods: {
    /**
     * Metode for å refreshe brukers reservasjonsoversikt
     */
    reloadRes(){
      this.reload = !this.reload;
    },
    openPage(pageName) {
      let i;
      let tabContent;
      tabContent = document.getElementsByClassName("tabContent");
      for (i = 0; i < tabContent.length; i++) {
        tabContent[i].style.display = "none";
      }
      document.getElementById(pageName).style.display = "block";
      document.getElementById("defaultOpen").click();
    },
    logout(){
      store.dispatch("logout");
    }

  }

}
</script>

<style scoped>
#aboutus {
  width: 62vw;
  margin: auto;
}

#aboutus b{
  font-size: xx-large;
}

#aboutus pre{
  font-size: large;
}

.tabset > input[type="radio"] {
  position: absolute;
  left: -200vw;
}

.tabset .tab-panel {
  display: none;
}

.tabset > input:first-child:checked ~ .tab-panels > .tab-panel:first-child,
.tabset > input:nth-child(3):checked ~ .tab-panels > .tab-panel:nth-child(2),
.tabset > input:nth-child(5):checked ~ .tab-panels > .tab-panel:nth-child(3),
.tabset > input:nth-child(7):checked ~ .tab-panels > .tab-panel:nth-child(4),
.tabset > input:nth-child(9):checked ~ .tab-panels > .tab-panel:nth-child(5),
.tabset > input:nth-child(11):checked ~ .tab-panels > .tab-panel:nth-child(6) {
  display: block;
}

.tabset > label {
  position: relative;
  display: inline-block;
  padding: 1rem 1rem 1rem;
  border: 1px solid transparent;
  border-radius: 3px;
  border-bottom: 0;
  cursor: pointer;
  font-weight: 600;
  width: 15rem;
}

.tabset > label::after {
  content: "";
  position: absolute;
  left: 15px;
  bottom: 10px;
  width: 20px;
  height: 4px;
  background: #a0a0a0;
}

.tabset > label:hover,
.tabset > input:focus + label {
  color: #17a9fa;
}

.tabset > label:hover::after,
.tabset > input:focus + label::after,
.tabset > input:checked + label::after {
  background: #17a9fa;
}

.tabset > input:checked + label {
  border-color: #ccc;
  border-bottom: 1px solid #fff;
  margin-bottom: -1px;
}

.tab-panel {
  padding: 30px;
  border-top: 1px solid #ccc;
  border-radius: 3px;
}

*,
*:before,
*:after {
  box-sizing: border-box;
}

.tabset {
  max-width: 100vw;
  margin: auto;
  overflow-y: hidden;
  margin-top: 2vh;
  border-radius: 3px;
}

#room{
  text-align: center;
}

#sectionHeader{
  margin-bottom: 2.5rem;
}

#logout{
  float: right;
  margin-right: 2rem;
}

</style>