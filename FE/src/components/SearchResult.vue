
<!-- This is where all the rooms based on the search is loaded-->

<template>
    <div v-if="validSearch">
        <Ripple v-if="isSearching"/>

        <div id="container" v-else>
            <div id="fieldbar">
                <div class="fields">
                    <p>Rom navn</p>
                </div>
                <div class="fields">
                    <p>Antall ledig seksjoner</p>
                </div>
            </div>
            <div id="noResult" v-show="noResult"><h2>Ingen rom tilgjengelig</h2></div>
            <Room v-for="(room,i) in rooms" :key="i" :room="room"
            :searchParameters="searchParameters"
            @refresh="getRooms"/>
        </div>
        
    </div>
    <div v-else id="badTime">Ugyldig tid oppgitt i søk</div>
</template>

<script>
import Room from "./Room.vue"
import createHttp from "@/services/http";
import Ripple from './Ripple.vue';
import moment from 'moment'

export default {
    components:{
        Room,
        Ripple
    },
    props:{
        searchParameters: Object,
        loadRooms: Boolean, //A boolean used to trigger the getRooms() method
    },
    data(){
        return{
            http: createHttp(),
            rooms:[],
            isSearching: false,
            validSearch: false,
            noResult: false,
        }
    },
    watch:{
        /**
         * Metode for å starte søket.
         * Sjekker først at tiden oppgitt av bruker ikke er i fortiden.
         */
        loadRooms: function(){

            let date = moment().format("YYYY-MM-DD");
            let startmoment = new moment(date + ' ' + this.searchParameters.startTime);

            let start = this.findMinutes(startmoment);
            let now = this.findMinutes(moment());
            
            if(moment().isSame(this.searchParameters.date, 'date') && start < now){ 
                console.log('nope');
            } else {
                this.validSearch = true;
                this.getRooms();
            }
            
        },

    },
    computed:{
        axiosParams(){
            const params = {
                date: this.searchParameters.date,
                fromTime:this.searchParameters.startTime,
                endTime: this.searchParameters.endTime,
                noOfPeople:this.searchParameters.numberOfPlaces,
                sameSection: this.searchParameters.sameSection
            }
            if(this.searchParameters.roomName != ""){
                params.roomName = this.searchParameters.roomName;
            }
            return params;
        }
    },
    methods:{
        findMinutes(m){
            return m.minutes() + m.hours() * 60;
        },
        getRooms(){
                this.isSearching = true;
                this.http.get(process.env.VUE_APP_API_URL + "/api/v1/reservations/search", {
                    params:this.axiosParams
                })
                .then(response => {
                    this.rooms = response.data;
                    this.isSearching = false;
                    if(this.rooms.length == 0){
                        this.noResult = true;
                    }else{
                        this.noResult = false;
                    }
                })
                .catch(err => {
                    console.log(err);
                    this.rooms = [];
                    this.noResult = true;
                    this.isSearching = false;
                })
            },
    }
}
</script>

<style scoped>
*{
    font-weight: bold;
}
#container{
    display: flex;
    flex-direction: column;
    height: 30rem;
    max-width: 71rem;
    margin: auto;
    margin-top: 2rem;
}

#badTime {
    color: red;
}
#noResult{
    margin-top: 2rem;
}

</style>