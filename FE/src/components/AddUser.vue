<!-- Component for creating users as an admin -->
<template>
    <div id="container">
        <div id="inputBox">
            <div id="header">
                <h1>Legg til ny bruker</h1>
            </div>
            <div id="inputs">
                <ul id="list">
                    <div class="inputFields">
                        <label>Fornavn:</label>
                        <input id="firstName"
                               v-model="firstName"
                               type="text"
                               name="firstName"
                               placeholder="Fornavn"
                               required
                        />
                    </div>
                    <div class="inputFields">
                        <label>Etternavn:</label>
                        <input id="lastName"
                               v-model="lastName"
                               type="text"
                               name="lastName"
                               placeholder="Etternavn"
                               required
                        />
                    </div>
                    <div class="inputFields">
                        <label>Epost:</label>
                        <input id="email"
                               v-model="email"
                               type="text"
                               name="email"
                               placeholder="Epost"
                               required
                        />
                    </div>
                    <div class="inputFields">
                        <label>Tlf nummer:</label>
                        <input id="phoneNumber"
                               v-model="phoneNumber"
                               type="text"
                               name="phoneNumber"
                               placeholder="Telefonnummer"
                               required
                        />
                    </div>
                    <div class="inputFields">
                        <label id="roleLabel">Rolle:</label>
                        <select v-model="roleIndex" @change="checkSelect">
                            <option>Bruker</option>
                            <option>Admin</option>
                        </select>
                    </div>
                </ul>
                <button class="btn" @click="submit">Legg til bruker</button>
            </div>
            <p v-if="wrongName" class= "error">
                <b>Fyll fullt navn</b>
            </p>
            <p v-if="wrongEmail" class= "error">
                <b>Fyll inn gyldig epost</b>
            </p>
            <p v-if="wrongPhoneNumber" class= "error">
                <b>Fyll inn gyldig telefonnummer</b>
            </p>
            <p v-if="missingRole" class= "error">
                <b>Velg rolle</b>
            </p>
            <p v-if="successfullSubmit === 1" class= "success">
                <b>Bruker lagt til</b>
            </p>
            <p v-if="successfullSubmit === 2" class= "error">
                <b>Noe gikk galt</b>
            </p>
        </div>
    </div>
</template>

<script>
    import utils from "../common/utils";
    import createHttp from "@/services/http";

    export default {
        name: "AddUser",
        data() {
            return {
                firstName: "",
                lastName: "",
                email: "",
                phoneNumber: "",
                roleIndex: "",
                selectedIndex: "",
                roleNames: ["Admin", "User"],
                role: [
                    {id: 1, name: "ADMIN"},
                    {id: 2, name: "USER"}
                    ],
                wrongName: false,
                wrongEmail: false,
                wrongPhoneNumber: false,
                missingRole: false,
                successfullSubmit: 0,
                http: createHttp(),
            };
        },
        methods: {
            //Method for finding index of selected role
            checkSelect() {
                this.selectedIndex = this.roleNames.indexOf(this.roleIndex)
                if (this.selectedIndex === -1) {
                    this.selectedIndex = 1
                }
            },

            //Method for creating new user
            submit() {
                this.successfullSubmit = 0;
                //Checks if name, email and phonenumber are correct and setting error messages
                this.checkName()
                this.checkEmail()
                this.checkPhoneNumber()
                this.checkRole()

                //Sets body for post request
                const bodyParameters = {
                    phoneNumber: this.phoneNumber,
                    firstname: this.firstName,
                    lastname: this.lastName,
                    email: this.email,
                    password: "passord",
                    role: {
                        id: this.role[this.selectedIndex].id,
                        name: this.role[this.selectedIndex].name
                    }
                };

                if (!this.wrongName && !this.wrongEmail && !this.wrongPhoneNumber){
                    //Sends post request to server for creating user
                    this.http.post(utils.apiUrl + "/api/v1/users", bodyParameters).then(response => {
                        console.log(response);
                        //Removes display and sets successfull submit message
                        this.firstName = "";
                        this.lastName = "";
                        this.email = "";
                        this.phoneNumber = "";
                        this.successfullSubmit = 1;
                    }).catch(error => {
                        console.log(error);
                        //Sets error message
                        this.successfullSubmit = 2;
                    });
                } else {
                    this.successfullSubmit = false;
                }
            },
            //Checks if name follows requirements and sets error message if not
            checkName() {
                if (!this.firstName || !this.lastName){
                    this.wrongName = true;
                } else {
                    this.wrongName = false;
                }
            },
            //Checks if email follows requirements and sets error message if not
            checkEmail() {
                let validRegex = /^[a-zA-Z0-9.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*$/;

                if (!this.email || !this.email.match(validRegex)){
                    this.wrongEmail = true;
                } else {
                    this.wrongEmail = false;
                }
            },
            //Checks if phone number follows requirements and sets error message if not
            checkPhoneNumber(){
                if (!this.phoneNumber || this.phoneNumber.length !== 8) {
                    this.wrongPhoneNumber = true;
                } else {
                    this.wrongPhoneNumber = false;
                }
            },

            //Checks if role is not selected
            checkRole() {
                if (!this.selectedIndex && this.selectedIndex !== 0) {
                    this.missingRole = true;
                } else {
                    this.missingRole = false;
                }
            }
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
    #list {
        list-style-type: none;
        position: relative;
        float: right;
        margin-bottom: 2rem;
    }
    .inputFields {
        margin-top: 15px;
        position: relative;
        display: inline-block;
        width: 100%;
    }
    .inputFields input {
        position: relative;
        float: right;
        right: 33%;
    }
    .inputFields select {
        position: relative;
        float: right;
        right: 33%;
        padding-left: 105px;
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