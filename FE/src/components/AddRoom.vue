<!-- Component for adding rooms with sections for admins -->
<template>
    <div id="container">
        <div id="inputBox">
            <div id="header">
                <h1>Legg til nytt rom</h1>
            </div>
            <div id="inputs">
                <div class="inputFields">
                    <label>Rom nummer:</label>
                    <input id="roomNumber"
                           v-model="roomNumber"
                           type="text"
                           name="roomNumber"
                           placeholder="Rom nummer"
                           required
                    />
                </div>
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
                </div>
                <p v-if="missingSectionTitle" class= "error">
                    <b>Fyll Seksjon tittel</b>
                </p>
                <p v-if="wrongRecomendedSpace" class= "error">
                    <b>Fyll riktig anbefalte plasser</b>
                </p>
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
                <hr>
                <button class="btn" @click="submit">Legg til rom</button>
            </div>
            <p v-if="missingRoomNumber" class= "error">
                <b>Fyll romnummer</b>
            </p>
            <p v-if="missingSection" class= "error">
                <b>Må ha minst en seksjon</b>
            </p>
            <p v-if="successfullSubmit === 1" class= "success">
                <b>Rom lagt til</b>
            </p>
            <p v-if="successfullSubmit === 2" class= "error">
                <b>Noe gikk feil</b>
            </p>
        </div>
    </div>
</template>

<script>
    import createHttp from "@/services/http";
    import utils from "../common/utils";

    export default {
        name: "AddRoom",
        data() {
            return {
                roomNumber: "",
                sectionTitle: "",
                recomendedSpace: 0,
                sections: [],
                missingRoomNumber: false,
                missingSection: false,
                missingSectionTitle: false,
                wrongRecomendedSpace: false,
                successfullSubmit: 0,
                http: createHttp(),

            };
        },
        methods: {
            //Method for adding section to a room
            addSection() {
                //Checks if sectionfields fulfills requirements
                this.checkSectionFields()

                //Adds section to room if section has title and correct reccomended space
                if (!this.missingSectionTitle && !this.wrongRecomendedSpace) {
                    this.sections.push({name: this.sectionTitle, suggestedAmountPeople: this.recomendedSpace});
                    this.sectionTitle = "";
                    this.recomendedSpace = 0;
                }
            },

            //Method for removing section from room
            removeSection(index) {
                this.sections.splice(index, 1);
            },

            //Method for submitting room with sections to server
            submit() {
                //Checks if requirements for a room is fulfilled
                this.checkInput()
                this.successfullSubmit = 0;

                const body = {
                    name: this.roomNumber,
                    sections: this.sections
                }

                //Sends post request to server if requirements for rooms are met
                if (!this.missingRoomNumber && !this.missingSection) {
                    this.http.post(utils.apiUrl + "/api/v1/rooms", body).then((response) => {
                        console.log(response);
                        //Removes displayment of the created room and sets message of successfull submit
                        this.roomNumber = "";
                        this.sections.splice(0, this.sections.length);
                        this.successfullSubmit = 1;
                    }).catch(error => {
                        console.log(error);
                        //Sets error message
                        this.successfullSubmit = 2;
                    });
                } else {
                    this.successfullSubmit = 2;
                }
            },

            //Method for checking requirements for adding section and setting error message
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
            },

            //Method for checking requirements for creating room and setting error message
            checkInput() {
                if (!this.roomNumber){
                    this.missingRoomNumber = true;
                } else {
                    this.missingRoomNumber = false;
                }

                if (this.sections.length < 1) {
                    this.missingSection = true;
                } else {
                    this.missingSection = false;
                }
            },
        },
    }
</script>

<style scoped>
    input {
        float: left;
        margin-left: 5px;
        padding: 2px 10px;
    }
    #container {
        position: relative;
        width: 100%;
        border-radius: 4px;
    }
    #inputBox {
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
    #inputs {
        position: relative;
        float: right;
        width: 100%;
        height: 100%;
        text-align: center;
    }
    .inputFields {
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
        margin-left: 5px;
    }
    .sectionInputFields input {
        margin-right: 40px;
    }
    .sectionInputFields label {
        position: relative;
        float: left;
        margin-left: 15px;
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
    .btn{
        margin-bottom: 15px;
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