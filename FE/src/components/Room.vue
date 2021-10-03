
<!--Each room is represented by a row and in a table
has it own set with sections, as well as a modal window to display 
these sections
 -->
<template>
    <div id="roomContainer">
        <div class="fields" id="roomName">
            <p>{{room.name}}</p>
        </div>
        <div class="fields" id="availablity">
            <p>{{availableSections}} / {{room.noOfSections}}</p>
            <div class="circle" :class="{noneAvailable: noSections}"></div>
        </div>
        <button class="btn bookBtn" :class="{cantBook: isDisabled}" :disabled="isDisabled" @click="bookEverything">Reserver alt</button>
        <button class="btn" id="sectionBtn" @click="showModal" :class="{cantBook: noSections}" :disabled="noSections">Se ledige seksjoner</button>

        <modal v-show="isShowingModal" @close="closeModal">
            <template v-slot:header>
                <h1>Seksjoner</h1>
            </template>

            <template v-slot:body>
                <div id="fieldbar">
                    <div class="fields">
                        <p>Seksjon navn</p>
                    </div>
                    <div class="fields">
                        <p>Anbefalt kapasitet</p>
                    </div>
                </div>
                <div id="allSectionContainer">
                    <Section v-for="(section,i) in sections" :key="i" :section="section"
                    :pos="i"
                    @selected="selectSection"
                    :resetCheckMark="resetCheckMark"/>
                </div>
                <button class="btn reserveSectionBtn" :class="{cantBook: isDisabledCounter}" :disabled="isDisabledCounter" @click="bookSection">Reserver Seksjon(er)</button>
            </template>
        </modal>    
    </div>
</template>

<script>
import modal from './Modal.vue';
import Section from './Section.vue'
import createHttp from "@/services/http";
import { createToast } from 'mosha-vue-toastify';
import 'mosha-vue-toastify/dist/style.css'

export default {
    components:{
        modal,
        Section
    },
    props:{
        room: Object,
        searchParameters: Object,
    },
    computed:{
        isDisabled(){
            return this.availableSections < this.room.noOfSections;
        },
        isDisabledCounter(){
            return this.selectedSectionCounter < 1;
        },
        availableSections(){
            return this.room.sections.length;
        },
        noSections(){
            return this.availableSections == 0;
        }
    },
    data(){
        return{
            http: createHttp(),
            isShowingModal:false,
            selectedSectionCounter:0,
            subject:0,
            sections:this.room.sections,
            isSelectedSection:[],
            selectedSections:[],
            resetCheckMark:false,
        }
    },
    watch:{
        /*
        Watch when the array with sections change to update
        'isSelectedSection'. 'isSelectedSection' is an array where each
        index represents the index in the 'sections' array, to know if
        a certain section is selected or not. 
        */
        sections: function(){
            this.isSelectedSection = [];
            for (let index = 0; index < this.sections.length; index++) {
                this.isSelectedSection.push(false);
            }
        }
    },
    methods:{
        showModal(){
            this.isShowingModal = true;
        },
        closeModal(){
            this.isShowingModal = false;
            this.resetCheckMark = !this.resetCheckMark;
        },
        selectSection(...args){
            const [pos, bool] = args;
            this.isSelectedSection[pos] = bool;
            if(bool){
                this.selectedSectionCounter ++;
            }else{
                this.selectedSectionCounter -= 1;
            }
        },
        /**
         * refresh() is used to trigger the getRooms(). 
         * Used after booking a room/section, to refresh 
         * the curent rooms after change.
         */
        refresh(){
            this.$emit("refresh");
        },
        bookEverything(){
            var message = "Dato: " + this.searchParameters.date + "\n"
            + "Start: Kl. " + this.searchParameters.startTime + "\n"
            + "Slutt: Kl. " + this.searchParameters.endTime + "\n" + "\n";
            message += "Bekreft reservasjon for hele rom: " + this.room.name;

            //Requests the user ID before creating a reservation
            if(confirm(message)){
                this.http.get(process.env.VUE_APP_API_URL + "/api/v1/sub")
                .then(response => {
                    this.subject = response.data;
                    var reservation = {
                        "startTime":this.searchParameters.startTime,
                        "endTime":this.searchParameters.endTime,
                        "date":this.searchParameters.date,
                        "sections":this.room.sections,
                        "roomName": this.room.name
                    };
                    this.http.post(process.env.VUE_APP_API_URL + "/api/v1/reservations/" + this.subject, reservation)
                    .then(response => {
                        createToast('Reservert!',{type:"success"});
                        this.refresh();
                        return response;
                    })
                    .catch(err => {
                        createToast('Ooops.. Noe gikk galt',{type:"danger"});
                        this.refresh();
                        return err;
                    })
                });
            }
        },
        bookSection(){
            this.selectedSections = [];
            for (let index = 0; index < this.isSelectedSection.length; index++) {
                const bool = this.isSelectedSection[index];

                if(bool){
                    this.selectedSections.push(this.sections[index]);
                }
            }

            var listedSections = "Dato: " + this.searchParameters.date + "\n"
            + "Start: Kl. " + this.searchParameters.startTime + "\n"
            + "Slutt: Kl. " + this.searchParameters.endTime + "\n" + "\n";
            listedSections += "Bekreft reservasjon av følgende seksjoner:\n";

            for (let index = 0; index < this.selectedSections.length; index++) {
                const section = this.selectedSections[index];
                listedSections += "• " + section.name + "\n";
            }


            if(confirm(listedSections)){
                //Requests the user ID before creating a reservation
                this.http.get(process.env.VUE_APP_API_URL + "/api/v1/sub")
                .then(response => {
                    this.subject = response.data;
                    var reservation = {
                        "startTime":this.searchParameters.startTime,
                        "endTime":this.searchParameters.endTime,
                        "date":this.searchParameters.date,
                        "sections":this.selectedSections,
                        "roomName": this.room.name
                    };
                    this.http.post(process.env.VUE_APP_API_URL + "/api/v1/reservations/" + this.subject, reservation)
                    .then(response => {
                        this.closeModal();
                        this.refresh();
                        createToast('Reservert!',{type:"success"});
                        return response;
                    })
                    .catch(err => {
                       createToast('Ooops.. Noe gikk galt',{type:"danger"});
                       this.closeModal();
                       this.refresh();
                       return err;
                    })
                });
            }
        },
    }

}
</script>

<style scoped>
p{
    display: inline-block;
}
#roomContainer{
    padding-top: 1rem;
    height: 5rem;
    width: 100%;
    display: flex;
    flex-direction: row;
    border-bottom:3px solid #f2f2f2;
    border-radius: 1px;
}

#roomName{
    color: black;
}

#availablity{
    font-weight: lighter;
}

.bookBtn{
    height: 3rem;
    background-color: #5ac18e;
}

#sectionBtn{
    height: 3rem;
    margin-left: 3rem;
}

.reserveSectionBtn{
    background-color: #5ac18e;
}

#allSectionContainer{
    height: 28rem;
    overflow: scroll;
}

.circle{
    width: 0.75rem;
    height: 0.75rem;
    background-color: green;
    border-radius: 10px;
    display: inline-block;
    margin-left: 0.8rem;
}

.noneAvailable{
    background-color: red;
}

.cantBook{
    background-color: #a0a0a0;
}

.cantBook:hover{
    cursor: not-allowed;
}

/* The Modal (background) */
.modal {
  display: none; /* Hidden by default */
  position: fixed; /* Stay in place */
  z-index: 1; /* Sit on top */
  left: 0;
  top: 0;
  width: 100%; /* Full width */
  height: 100%; /* Full height */
  overflow: auto; /* Enable scroll if needed */
  background-color: rgb(0,0,0); /* Fallback color */
  background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
}

/* Modal Content/Box */
.modal-content {
  background-color: #fefefe;
  margin: 15% auto; /* 15% from the top and centered */
  padding: 20px;
  border: 1px solid #888;
  width: 80%; /* Could be more or less, depending on screen size */
}

/* The Close Button */
.close {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
}

.close:hover,
.close:focus {
  color: black;
  text-decoration: none;
  cursor: pointer;
}
</style>