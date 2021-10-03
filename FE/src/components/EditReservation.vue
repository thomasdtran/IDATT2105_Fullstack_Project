<!-- Component for editing reservations for admins -->
<template>
    <div id="container">
        <div id="reservationsBox">
            <div id="header">
                <h1>Endre reservasjoner</h1>
            </div>
            <div id="searchField">
                <div id="name">
                    <p>Romnavn:</p>
                    <input
                            id="nameInput"
                            v-model="name"
                            type="text"
                            placeholder="Eks: A1-12"
                            name="Navn"
                            maxlength="40"
                    />
                </div>
                <div id="date">
                    <p>Dato:</p>
                    <input
                            v-model="date"
                            type="date"
                            name="Dato"
                            v-bind:min="today"
                            v-bind:max="maxDate"
                            required
                    />
                </div>
                <button class="btn" @click="emptySearch">Tilbakestill søk</button>
                <button class="btn" @click="filterReservations">Filtrer</button>
            </div>
            <div id="reservationList">
                <p v-if="successfullDelete === 1" class= "success">
                    <b>Reservasjon slettet</b>
                </p>
                <p v-if="successfullDelete === 2" class= "error">
                    <b>Noe gikk feil</b>
                </p>
                <hr>
                <ul>
                    <li v-for="reservation in displayedReservations" :key="reservation">
                        <div class="reservations">
                            <p>{{"Rom: " + reservation.roomName}}</p>
                            <p>{{"Tidspunkt: " + reservation.date + ", " + reservation.startTime + "-" + reservation.endTime}}</p>
                            <p>{{"Antall sekjsoner: " + reservation.sections.length}}</p>
                            <p>{{"Reservert av: " + reservation.user.firstname + " " + reservation.user.lastname}}</p>
                            <div id="functions">
                                <p id="delete" @click="deleteClick(reservation.id)">Slett</p>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>

<script>
    import utils from "../common/utils";
    import moment from 'moment';
    import createHttp from "@/services/http";

    export default {
        name: "EditReservation",
        data() {
            return {
                reservations: [],
                displayedReservations: [],
                name: "",
                date: "",
                successfullDelete: 0,
                http: createHttp(),
            };
        },
        methods: {
            //Method for retrieving all reservations and updating site
            updateList() {
                //Get request to server
                this.http.get(utils.apiUrl + "/api/v1/reservations").then(res => {
                    this.reservations = res.data;
                    this.displayedReservations = this.reservations;
                })
            },
            //Method for prompt if admin wants to delete a reservation
            deleteClick(id) {
                this.successfullDelete = 0;
                //Confirm propmt that callsdeleteReservation if pressed
                if (confirm("Er du sikker på at du vil slette reservasjonen?")) {
                    this.deleteReservation(id)
                }
            },

            //Method for deleting a reservation. Takes Id of reservation as parameter
            deleteReservation(id) {
                //Delete request to server
                this.http.delete(utils.apiUrl + "/api/v1/reservations/admin/" + id).then(res => {
                    console.log(res);
                    //Sets successfull message and updates display of reservations
                    this.successfullDelete = 1;
                    this.updateList();
                }).catch(err => {
                    console.log(err);
                    //Sets error message
                    this.successfullDelete = 2;
                });
            },

            //Method for filtering reservations by search inputs
            filterReservations() {
                this.displayedReservations = [];
                //If there are no inputs, displays all reservations
                if (!this.date && !this.name) {
                    this.displayedReservations = this.reservations;
                }
                //If there is a name but no date, displays only reservations with matching name
                else if (!this.date && this.name) {
                    for (let reservation of this.reservations) {
                        if (reservation.roomName.toLowerCase().includes(this.name.toLowerCase())) {
                            this.displayedReservations.push(reservation);
                        }
                    }
                }
                //If there is a date but no name, displays only reservation with matching date
                else if (this.date && !this.name) {
                    //Writes searched date in format year, month, date
                    moment.defaultFormat = "YYYY-MM-DD";
                    let searchedDate = moment(this.date, moment.defaultFormat).toDate()
                    for (let reservation of this.reservations) {
                        //Writes reservation date in same format
                        let reservationDate = moment(reservation.date, moment.defaultFormat).toDate()
                        if (moment(searchedDate).isSame(reservationDate)) {
                            this.displayedReservations.push(reservation);
                        }
                    }
                }
                //If there is both a name and a date, displays only reservations matching both
                else {
                    //Writes searched date in format year, month, date
                    moment.defaultFormat = "YYYY-MM-DD";
                    let searchedDate = moment(this.date, moment.defaultFormat).toDate()
                    for (let reservation of this.reservations) {
                        //Writes reservation date in same format
                        let reservationDate = moment(reservation.date, moment.defaultFormat).toDate()
                        if (moment(searchedDate).isSame(reservationDate)
                        && reservation.roomName.toLowerCase().includes(this.name.toLowerCase())) {
                            this.displayedReservations.push(reservation);
                        }
                    }
                }
            },

            //Empties search and displays all reservations
            emptySearch() {
                this.name = "";
                this.date = "";
                this.displayedReservations = this.reservations;
            },
        },
        computed: {
            //Finds current date
            today: function () {
                let today = new Date();
                let dd = String(today.getDate()).padStart(2, "0");
                let mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
                let yyyy = today.getFullYear();
                today = yyyy + "-" + mm + "-" + dd;
                return today;
            },
            //Sets max date for reservtions
            maxDate: function () {
                let today = new Date();
                let dd = String(today.getDate() + 14).padStart(2, "0");
                let mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
                let yyyy = today.getFullYear();
                today = yyyy + "-" + mm + "-" + dd;
                return today;
            },
        },
        async mounted() {
            //Updates reservations list when mounted
            this.updateList();
        }
    }
</script>

<style scoped>
    ul {
        list-style-type: none;
    }
    #container{
        position: relative;
        width: 100%;
        z-index: 1;
    }
    #reservationsBox {
        position: relative;
        margin: 0 auto;
        top: 20px;
        margin-bottom: 20px;
        background-color: papayawhip;
        height: 100%;
        width: 60%;
        display: grid;
    }
    #header {
        position: relative;
        width: 100%;
        text-align: center;
        background-color: #17a9fa;
    }
    #header h1 {
        font-size: 36px;
    }
    #searchField {
        position: relative;
        width: 100%;
    }
    #searchField input {
        float: left;
        margin-left: 5px;
        padding: 0px 0px;
        text-align: center;
    }
    #name {
        float: left;
        margin: 0 auto;
        display: flex;
        flex-direction: row;
        margin-top: 20px;
        margin-left: 20px;
    }
    #date {
        float: left;
        display: flex;
        flex-direction: row;
        margin-top: 20px;
        margin-left: 40px;
    }
    .btn {
        position: relative;
        float: right;
        margin-right: 40px;
        top: 20px;
    }
    #reservationList{
        position: relative;
        width: 100%;
        height: 100%;
        text-align: center;
    }
    .reservations{
        position: relative;
        width: 320px;
        height: 100%;
        background-color: lightblue;
        margin: 0 auto;
        margin-bottom: 40px;
        right: 30px;
    }
    #functions {
        width: 100%;
        display: flex;
    }
    #delete {
        position: relative;
        float: right;
        color: blue;
        margin-left: 45%;
        text-decoration: underline;

    }
    #delete:hover {
        cursor: pointer;
    }
    .error {
        position: relative;
        width: 100%;
        font-size: 0.8rem;
        color: red;
        text-align: center;
    }
    .success {
        position: relative;
        width: 100%;
        font-size: 0.8rem;
        color: green;
        text-align: center;
    }
</style>