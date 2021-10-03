<template>
    <div id="container">
            <p v-if="missingDate" class= "error">
                <b>Fyll inn riktig dato</b>
            </p>
            <p v-if="distantDate" class= "error">
                <b>Kan kun booke rom 2 uker fram i tid</b>
            </p>
            <p v-if="missingTime" class= "error">
                <b>Fyll inn gyldig start og slutttidspunkt</b>
            </p>
            <p v-if="missingNumberOfPlaces" class= "error">
                <b>Fyll inn gyldig antall nødvendige plasser</b>
            </p>

        <div id="searchField">
            <div class="line">
            <div class="lineContainer">
            <div id="date">
                <p>Dato</p>
                <input
                        v-model="searchParameters.date"
                        type="date"
                        name="Dato"
                        v-bind:min="today"
                        v-bind:max="maxDate"
                        required
                />
            </div>
                <div id="startTime">
                    <p>Start</p>
                    <select name="startTime" class="startTime" v-model="searchParameters.startTime">
                        <option v-for="(time,i) in listOfTimesStart" :key="i" :value="time">{{time}}</option>
                    </select>
                </div>
                <div id="endTime">
                    <p>Slutt</p>
                    <select name="endTime" class="endTime" v-model="searchParameters.endTime">
                        <option v-for="(time,i) in listOfTimesEnd" :key="i" :value="time">{{time}}</option>
                    </select>
                </div>
            </div>
            </div>
            <div class="line">
                <div class="lineContainer">
                <div id="numberOfPlaces">
                    <p>Minimum antall plasser</p>
                    <input
                            v-model="searchParameters.numberOfPlaces"
                            type="number"
                            min="1"
                            max="200"
                            name="Plasser"
                            required
                    />
                </div>
                <div id="name">
                    <p>Rom navn</p>
                    <input
                            id="nameInput"
                            v-model="searchParameters.roomName"
                            type="text"
                            placeholder="Navn (valgfritt)"
                            name="Navn"
                            maxlength="40"
                    />
                </div>
                <div id="sameSection">
                    <p>Alle må ha plass i samme seksjon</p>
                    <input
                            id="checkmark"
                            v-model="searchParameters.sameSection"
                            type="checkbox"
                            name="sameSection"
                    />
                </div>
            </div>
            </div>
            <div class="line">
                <div id="buttonLine">
                    <button class="btn" @click="search">Søk</button>
                </div>
            </div>
        </div>
    </div>
    
    <SearchResult v-show="validSearch" 
    :searchParameters="searchParameters" 
    :loadRooms="loadRooms"/>

</template>

<script>
    import moment from 'moment';
    import SearchResult from './SearchResult.vue'

    export default {
        name: 'SearchField',
        components:{
            SearchResult,
        },
        data() {
            return {
                searchParameters:{
                    "startTime":"07:00",
                    "endTime":"08:00",
                    "date": new Date().toISOString().substr(0, 10),
                    "numberOfPlaces": 1,
                    "sameSection": false,
                    "roomName": "",
                },
                missingDate: false,
                distantDate: false,
                missingTime: false,
                missingNumberOfPlaces: false,
                validSearch: false,
                isSearching: false,
                loadRooms: false, //Used to trigger getRooms() in SearchResult.vue
            };
        },
        methods: {
            search(){
                this.sections = [];
                this.validSearch = false;
                this.checkWrongDate();
                this.checkWrongTime();
                this.checkMissingInput();

                if(this.missingDate || this.distantDate || this.missingTime || this.missingNumberOfPlaces){
                    this.validSearch = false;
                }else{
                    this.validSearch = true;
                    this.loadRooms = !this.loadRooms;
                }
            },
            checkWrongDate() {
                moment.defaultFormat = "YYYY-MM-DD";
                let nowDate = moment(moment().format(moment.defaultFormat)).toDate();
                let reservationDate = moment(this.searchParameters.date, moment.defaultFormat).toDate();

                let before = false;
                if (moment(reservationDate).isBefore(nowDate)){
                    before = true;
                }

                let distantDate = moment(nowDate).add(14, 'days').format("YYYY-MM-DD");

                let tooDistant = false;
                if (moment(distantDate).isBefore(reservationDate)) {
                    tooDistant = true;
                }

                if (!this.searchParameters.date || before){
                    this.missingDate = true;
                } else if (tooDistant) {
                    this.distantDate = true;
                    this.missingDate = false;
                } else {
                    this.missingDate = false;
                    this.distantDate = false;
                }
            },

            checkWrongTime() {
                moment.defaultFormat = "HH:mm"

                let start = moment(this.searchParameters.startTime, moment.defaultFormat).toDate();
                let end = moment(this.searchParameters.endTime, moment.defaultFormat).toDate();

                let before = false;
                if (moment(end).isBefore(start)){
                    before = true;
                }

                if (!this.searchParameters.startTime || !this.searchParameters.endTime || before){
                    this.missingTime = true;
                } else {
                    this.missingTime = false;
                }
            },

            checkMissingInput(){
                if (!this.searchParameters.numberOfPlaces || this.searchParameters.numberOfPlaces <= 0){
                    this.missingNumberOfPlaces = true;
                } else {
                    this.missingNumberOfPlaces = false;
                }
            },
             currentTime(){
                var today = new Date(),
                h = (today.getHours()<10?'0':'') + today.getHours(),
                m = (today.getMinutes()<10?'0':'') + today.getMinutes();

                var time = h + ":" + m;
                return time;
            },
            currentTimePlusOneHour(){
                var today = new Date(),
                h = ((today.getHours())<10?'0':'') + (today.getHours() + 1),
                m = (today.getMinutes()<10?'0':'') + today.getMinutes();
                var time = h + ":" + m;
                return time;
            }
        },
        computed: {
            today: function () {
                let today = new Date();
                let dd = String(today.getDate()).padStart(2, "0");
                let mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
                let yyyy = today.getFullYear();
                today = yyyy + "-" + mm + "-" + dd;
                return today;
            },
            maxDate: function () {
                let today = new Date();
                let dd = String(today.getDate() + 14).padStart(2, "0");
                let mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
                let yyyy = today.getFullYear();
                today = yyyy + "-" + mm + "-" + dd;
                return today;
            },
            listOfTimesStart(){
                var list = [];
                var hoursInADay = 24;
                var minIncrement = 15; //The booking times is incremented på 15 minutes
                var minInHour = 60;

                for (let i = 6; i < hoursInADay; i++) {
                    var hour = "";
                    if(i < 10){ //Check to format the time correctly
                        hour = "0" + i;
                    }else{
                        hour = i;
                    }

                    for (let j = 0; j < minInHour; j+=minIncrement) {
                        var min = "";
                        if(j < 10){
                            min = "0" + j;
                        }else{
                            min = j;
                        }
                        var time = hour + ":" + min;
                        list.push(time);
                    }
                }
                return list;
            },
            listOfTimesEnd(){
                /*
                List of choosable times in the endtime starts 1 hour
                after the chosen start-time. 
                */
                var list = [];
                var hoursInADay = 24;
                var minIncrement = 15;
                var minInHour = 60;

                //Splitting up the start time
                var minTime = (this.searchParameters.startTime).split(":");

                //Check to get the correct number, becuase hours less than '10' starts with '0'
                var minHour = (parseInt(minTime[0].charAt(0) + 1) == 0 
                ? parseInt(minTime[0].charAt(1)) + 1 : parseInt(minTime[0]) + 1);

                //Same with minute --||--
                var minMinute = ((minTime[1].charAt(0)) == 0 
                ? parseInt(minTime[1].charAt(1)) : parseInt(minTime[1]));

                //Set ups all the increments for the first hour
                for (let i = minMinute; i < minInHour; i+=minIncrement) {
                    var nextMin = "";
                    if(i < 10){
                        nextMin = "0" + i;
                    }else{
                        nextMin = i;
                    }
                    if(minHour < 10){
                         var nextTime = "0" + minHour + ":" + nextMin;
                    }else nextTime = minHour + ":" + nextMin;
                    list.push(nextTime);
                }

                //Set ups all the other times after the first hour
                for (let i = minHour + 1; i < hoursInADay; i++) {
                    var hour = "";
                    if(i < 10){
                        hour = "0" + i;
                    }else{
                        hour = i;
                    }

                    for (let j = 0; j < minInHour; j+=minIncrement) {
                        var min = "";
                        if(j < 10){
                            min = "0" + j;
                        }else{
                            min = j;
                        }
                        var time = hour + ":" + min;
                        list.push(time);
                    }
                }
                return list;
            }
        }
    }
</script>

<style scoped>
    input, .startTime, .endTime{
        padding-left: 0.8rem;
        border: none;
        border-radius: 4px;
        height: 2.8rem;
        min-width: 13rem;
        border: 2px solid #e0e0e0;
        font-size: 0.9rem;
        transition: border-color 0.3s ease;
        outline: none;
    }
    
    input:focus{
        border-color: #707070;
    }
    .startTime:focus{
        border-color: #707070;
    }
    .endTime:focus{
        border-color: #707070;
    }
    #container {
        position: relative;
        width: 100%;
    }
    #searchField {
        margin-left: 10%;
        margin-right: 10%;
        position: relative;
        border-radius: 4px;
        height: 100%;
        display: grid;
    }
    .line {
        max-width: 100%;
        display: flex;
        flex-direction: row;
        align-items: center;
    }
    #startTime,#date,#endTime, #name{
        display: flex;
        flex-direction: column;
        margin: 1rem;
        text-align: left;
    }

    .timeInput {
        width: 11rem;
        height: 3rem;
    }
    #numberOfPlaces {
        display: flex;
        flex-direction: column;
        margin: 1rem;
    }
    #numberOfPlaces p {
        display: inline;
        overflow: hidden;
        white-space: nowrap;
    }
    #numberOfPlaces input {
        width: 10rem;
    }
    #nameInput{
        width: 11rem;
    }
    #sameSection{
        display: flex;
        flex-direction: column;
        margin: 1rem;
    }
    #checkmark {
        position: relative;
        top: 0;
        left: 0;
        height: 25px;
        width: 25px;
        background-color: #eee;
    }
    #buttonLine{
        float: left;
        margin: 0 auto;
        display: flex;
        flex-direction: row;
        margin-top: 1.8rem;
        margin-left: 45%;
        margin-right: 45%;
        margin-bottom: 1rem;
    }
    #searchButton{
        background-color: #17a9fa; /* Green */
        border: none;
        color: white;
        padding: 10px 21px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 16px;
        border: none;
        border-radius: 15px;
        box-shadow: 0 6px #999;
    }
    #searchButton:hover{
        cursor: pointer;
    }
    #searchButton:active{
        box-shadow: 0 3px #666;
        transform: translateY(3px);
    }
    .error {
        position: relative;
        width: 100%;
        font-size: 0.8rem;
        color: red;
        text-align: center;
        margin: 0;
        margin-top: 1rem;
    }
    .lineContainer{
        display: flex;
        margin: auto;
        max-width: 75%;
        min-width: 75%;
        padding-left: 1rem;
    }
</style>