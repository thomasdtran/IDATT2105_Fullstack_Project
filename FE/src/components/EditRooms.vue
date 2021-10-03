<!-- Component for editing rooms for admins -->
<template>
    <div id="container">
        <div id="reservationsBox">
            <div id="header">
                <h1>Rediger rom</h1>
            </div>
            <div id="searchField">
                <div id="name">
                    <p>Romnavn:</p>
                    <input
                            id="nameInput"
                            v-model="searchedName"
                            type="text"
                            placeholder="Eks: A1-12"
                            name="Navn"
                            maxlength="40"
                            @keypress.enter="searchForName"
                    />
                </div>
                <button class="btn" @click="searchForName">Søk</button>
            </div>
            <div id="roomList">
                <p v-if="roomDeleted" class= "success">
                    <b>Rom slettet</b>
                </p>
                <p v-if="roomDeleted === 2" class= "error">
                    <b>Noe feilet</b>
                </p>
                <hr>
                <ul>
                    <li>
                        <div class="rooms" v-for="room in displayedRooms" :key="room">
                            <p>{{"Romnummer: " + room.name}}</p>
                            <p>{{"Antall sekjsoner: " + room.sections.length}}</p>
                            <div id="functions">
                                <p id="edit" @click="editClick(room)">Rediger</p>
                                <p id="delete" @click="deleteClick(room)">Slett</p>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>

    <div id="blackBackground" v-if="editMode"></div>
    <div id="editBox" v-if="editMode">
        <div id="editHeader">
            <h2 id="editTitle">{{roomNumber}}</h2>
            <h2 id="exit" @click="exitEdit">X</h2>
        </div>
        <hr>
        <div id="inputFields">
            <div class="inputFields">
                <label>Navn:</label>
                <input id="roomNumber"
                       v-model="roomNumber"
                       type="text"
                       name="roomNumber"
                       placeholder="Rom nummer"
                       required
                />
            </div>
            <hr>
            <div class="sectionFields">
                <div class="sectionInputFields">
                    <label>Seksjon tittel:</label>
                    <input id="sectionTitel"
                           v-model="sectionTitle"
                           type="text"
                           name="sectionTitle"
                           placeholder="Seksjon tittel"
                           required
                    />
                </div>
                <div class="sectionInputFields">
                    <label>Anbefalt plass:</label>
                    <input id="recomendedSpace"
                           v-model="recomendedSpace"
                           type="number"
                           min="1"
                           max="200"
                           name="recomendedSpace"
                           required
                    />
                </div>
                <button id="addSectionBtn" @click="addSection">Legg til seksjon</button>
                <p v-if="missingSectionTitle" class= "error">
                    <b>Fyll Seksjon tittel</b>
                </p>
                <p v-if="wrongRecomendedSpace" class= "error">
                    <b>Fyll riktig anbefalte plasser</b>
                </p>
                <p v-if="missingSections" class= "error">
                    <b>Må ha minst 1 seksjon</b>
                </p>
                <hr>
            </div>
            <div class="displaySections">
                <ol>
                    <li v-for="(section, index) in sections" :key="section">
                        <div id="addedSections">
                            <p>{{"Tittel: " + section.name + ", Anbefalt plass: " + section.suggestedAmountPeople}}</p>
                            <p id="removeSection" @click="removeSection(index)">X</p>
                        </div>
                    </li>
                </ol>
            </div>
            <button class="btn" @click="updateRoom()">Endre</button>
            <p v-if="roomUpdated === 1" class= "success">
                <b>Rom Oppdatert</b>
            </p>
            <p v-if="roomUpdated === 2" class= "error">
                <b>Noe feilet</b>
            </p>
        </div>
    </div>
</template>

<script>
    import utils from "../common/utils";
    import createHttp from "@/services/http";

    export default {
        name: "EditRooms",
        data() {
            return {
                editMode: false,
                searchedName: "",
                roomNumber: "",
                rooms: [],
                displayedRooms: [],
                sections: [],
                currentRoomId: -1,
                sectionTitle: "",
                recomendedSpace: 0,
                originalSections: 0,
                temporarySections: 0,
                missingSectionTitle: false,
                missingSections: false,
                wrongRecomendedSpace: false,
                roomDeleted: 0,
                roomUpdated: 0,
                http: createHttp(),
            };
        },
        methods: {
            //Method for searching for rooms by name
            searchForName() {
                this.displayedRooms = [];

                //If no input then display all rooms
                if (!this.searchedName) {
                    this.displayedRooms = this.rooms
                } else {
                    //Displays rooms with matching names
                    for (let room of this.rooms) {
                        if (room.name.toLowerCase().includes(this.searchedName.toLowerCase())) {
                            this.displayedRooms.push(room);
                        }
                    }
                }
            },

            //Method for prompt if admin wants to delete a reservation. Takes object of room as parameter
            deleteClick(room) {
                if (confirm("Er du sikker på at du vil slette rom " + room.name + " med alle seksjoner?")) {
                    this.roomDeleted = 0;
                    this.deleteRoom(room.id)
                }
            },

            //Method for deleting room. Takes room id as parameter
            deleteRoom(id) {
                //Delete request to server with room id
                this.http.delete(utils.apiUrl + "/api/v1/rooms/" + id).then(res => {
                    console.log(res);
                    //sets success mesage and updates displayment of rooms
                    this.roomDeleted = 1;
                    this.updateList();
                }).catch(err => {
                    console.log(err)
                    //sets error message
                    this.roomDeleted= 2;
                });
            },

            //Method for updating displayed list of rooms
            updateList() {
                //Get request to server to retrieve all rooms
                this.http.get(utils.apiUrl + "/api/v1/rooms").then(res => {
                    this.rooms = res.data;
                    this.displayedRooms = this.rooms;
                }).catch(err =>{
                    console.log(err)
                });
            },

            //Method for displaying edit mode if edit button on room is clicked
            editClick(room) {
                this.editMode = true;
                this.roomNumber = room.name;
                this.sections = room.sections;
                this.originalSections = room.sections.length;
                this.currentRoomId = room.id;
            },

            //Method for updating room
            updateRoom() {
                //Checks if room name exists
                if (!this.roomNumber) {
                    this.missingSectionTitle = true;
                }
                //Checks if no sections exists
                if (this.sections.length < 1) {
                    this.missingSections = true;
                }
                //checks if both roomNumber and sections exists
                if (this.roomNumber && this.sections.length > 0) {
                    //Clears success/error message
                    this.roomUpdated = 0;
                    //Sends Put request to server for updating room with currentRoomId
                    this.http.put(utils.apiUrl + "/api/v1/rooms/" + this.currentRoomId, {
                        name: this.roomNumber,
                        sections: this.sections
                    }).then(res => {
                        console.log(res);
                        //Sets success message
                        this.roomUpdated = 1;
                    }).catch(err => {
                        console.log(err);
                        //Sets error message
                        this.roomUpdated = 2;
                    });
                }
            },

            //Method for exiting edit mode
            exitEdit() {
                this.updateList()
                this.editMode = false;
                this.roomNumber = "";
                this.currentRoomId = -1;
                this.originalSections = 0;
                this.temporarySections = 0;
                this.roomUpdated = 0;
            },

            //Method for adding section to room while in edit mode
            addSection() {
                //Checks if input are correct and sets error messages
                this.checkSectionFields()

                //Adds section to room if inputs are valid
                if (!this.missingSectionTitle && !this.wrongRecomendedSpace) {
                    this.sections.push({name: this.sectionTitle, suggestedAmountPeople: this.recomendedSpace});
                    this.sectionTitle = "";
                    this.recomendedSpace = 0;
                    this.temporarySections++;
                    this.missingSections = false;
                    this.roomUpdated = false;
                }
            },

            //Method for removing section from room
            removeSection(index) {
                this.sections.splice(index, 1);
            },

            //Method for checking if sectionFields are valid and sets error messagesif not
            checkSectionFields() {
                if (!this.sectionTitle) {
                    this.missingSectionTitle = true;
                } else {
                    this.missingSectionTitle = false;
                }
                if (!this.recomendedSpace || this.recomendedSpace < 1){
                    this.wrongRecomendedSpace = true;
                } else {
                    this.wrongRecomendedSpace = false;
                }
                let stringNumber = this.recomendedSpace.toString();
                this.recomendedSpace = parseInt(stringNumber, 10)
            },
        },
        //Updates displayed list of rooms when mounted
        async mounted() {
            this.updateList();
        },
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
        border-radius: 4px;
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
        margin-left: 23%;
        margin-right: 40px;
    }
    .btn {
        position: relative;
        float: left;
        margin-right: 40px;
        top: 20px;
    }
    #roomList{
        position: relative;
        width: 100%;
        height: 100%;
        text-align: center;
    }
    .rooms{
        position: relative;
        width: 320px;
        height: 160px;
        background-color: lightblue;
        margin: 0 auto;
        right: 30px;
    }
    #functions {
        width: 100%;
        display: flex;
    }
    #edit {
        position: relative;
        float: left;
        color: blue;
        margin-left: 50px;
        text-decoration: underline;
    }
    #edit:hover {
        cursor: pointer;
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
    #blackBackground{
        position: fixed;
        z-index: 2;
        background-color: black;
        opacity: 0.4;
        height: 100vh;
        width: 100vw;
        top: 0;
    }
    #editBox {
        position: absolute;
        background-color: mintcream;
        width: 400px;
        opacity: 1;
        top: 100px;
        left: 35vw;
        z-index: 3;
    }
    #editHeader {
        position: relative;
        display: inline-block;
        width: 100%;
        height: 50px;
    }
    #editTitle {
        position: relative;
        float: left;
        text-align: center;
        left: 35%;

    }
    #exit {
        position: relative;
        float: right;
        color: red;
        right: 15px;
    }
    #exit:hover {
        cursor: pointer;
    }
    #inputFields {
        margin-top: 15px;
        margin-bottom: 25px;
        position: relative;
        display: inline-block;
        width: 100%;
    }
    .inputFields input {
        position: relative;
        float: right;
        right: 33%;
    }
    .inputFields label {
        position: relative;
        margin-left: 55px;
    }
    .sectionInputFields input {
        position: relative;
        margin-left: 40px;
    }
    .sectionInputFields label {
        position: relative;
        float: left;
        margin-left: 15px;
    }
    #addSectionBtn {
        position: relative;
        margin-top: 15px;
        margin-left: 130px;
    }
    #addSectionBtn:hover {
        cursor: pointer;
    }
    .displaySections p {
        display: inline-block;
    }
    #removeSection {
        position: relative;
        color: red;
        float: right;
        right: 20%;

    }
    #removeSection:hover {
        cursor: pointer;
    }
    .btn {
        position: relative;
        left: 150px;
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