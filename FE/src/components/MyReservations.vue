<template>

    <Ripple v-if="isSearching"/>
    <div id="container" v-else>
        <div id="bar">
            <div class="f">
                <p>Rom</p>
            </div>
            <div class="f">
                <p>Seksjoner</p>
            </div>
            <div class="f">
                <p>Dato</p>
            </div>
            <div class="f">
                <p>Tid</p>
            </div>
        </div>

        <div v-for="r in reservations" :key="r" class="res">
            <div id="rname" class="d">
                <p>{{r.roomName}}</p>
            </div>
            <div id="rsections" class="d">
                <p v-for="s in r.sections" :key="s" class="d">{{s.name}}</p>
            </div>
            <div id="rname" class="d">
                <p>{{r.date}}</p>
            </div>
            <div id="rname" class="d">
                <p>{{r.startTime}} - {{r.endTime}}</p>
            </div>
            <div id="deleteReservation" @click="deleteReservationPrompt(r)">
                <button class="btn">X</button>
            </div>
        </div>
    </div>
</template>

<script>
import createHttp from "@/services/http";
import Ripple from './Ripple.vue'
export default {
    data(){
        return {
            http: createHttp(),
            reservations: [],
            isSearching: false,
        }
    },
    components:{
        Ripple
    },
    props: {
        reload: Boolean
    },
    watch:{
        reload: function(){
            this.load();
        }
    },
    methods:{
        /**
         * Henter ut alle reservasjonene til påloggede bruker og viser dem
         */
        async load(){
            let thisUserId;
            this.reservations = [];
            this.isSearching = true;
            await this.http.get(process.env.VUE_APP_API_URL + "/api/v1/sub")
            .then(res => {
                thisUserId = res.data;
            });
            await this.http.get(process.env.VUE_APP_API_URL + "/api/v1/reservations/user/"+thisUserId)
            .then(res => {
                res.data.forEach(element => {
                    this.reservations.push(element);
                });
                this.isSearching = false
            });
            
        },

        deleteReservationPrompt(reservation) {
            if (confirm("Er du sikker på at du vil slette reservasjonen av rom " + reservation.roomName + "?")) {
                this.deleteReservation(reservation.id)
            }
        },
        deleteReservation(id) {
            this.http.delete(process.env.VUE_APP_API_URL + "/api/v1/reservations/" + id).then(res => {
                console.log(res)
                this.load();
            }).catch(err => {
                console.log(err)
            });
        }
    },
    async mounted(){
        this.load();        
    }
}
</script>

<style scoped>

.lds-ripple {
  margin-top: 12rem;
}

#bar{
    display: flex;
    flex-direction: row;
    height: 3.5rem;
    background-color: #f2f2f2;
    border-radius: 20px 20px 0px 0px;
}

.f{
    width: 21rem;
    text-align: left;
    padding-left: 1rem;
    color: #898989;
}

.res{
    display: flex;
    flex-direction: row;
    border-bottom:3px solid #f2f2f2;
    border-radius: 1px;
}

.d{
    padding-left: 1rem;
    width: 25%;
}

.d p{
    padding-left: 0;
    width: 100%;
}

#deleteReservation {
    float: right;
    margin-right: 30px;
    color: 	#B22222;
}

.btn {
    background-color: #f44336;
    padding: 8px 16px;
    margin-top: 8px;
}
</style>