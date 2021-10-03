<!-- Component for finding user information and deleting users for admins -->
<template>
    <div id="container">
        <div id="inputBox">
            <div id="header">
                <h1>Finn bruker</h1>
            </div>
            <div id="searchField">
                <div class="inputs">
                    <p>Navn:</p>
                    <input
                            id="nameInput"
                            v-model="name"
                            type="text"
                            placeholder="Navn"
                            name="Name"
                            maxlength="60"
                    />
                </div>
                <div class="inputs">
                    <p>Epost:</p>
                    <input
                            id="emailInput"
                            v-model="email"
                            type="text"
                            placeholder="Epost"
                            name="Email"
                            maxlength="40"
                    />
                </div>
                <button class="btn" @click="search">Filtrer</button>
            </div>
            <div id="userList">
                <hr>
                <ul>
                    <li class="list">
                        <div class="userInfo">
                            <p class="name">Name:</p>
                            <p class="email">Epost:</p>
                            <p class="phoneNumber">Telefonnummer:</p>
                        </div>
                        <hr>
                    </li>
                    <li class="list" v-for="user in displayedUsers" :key="user">
                        <div class="userInfo">
                            <p class="name">{{user.firstname + " " + user.lastname}}</p>
                            <p class="email">{{user.email}}</p>
                            <p class="phoneNumber">{{user.phoneNumber}}</p>
                            <p id="deleteButton" @click="deletePrompt(user)">X</p>
                        </div>
                        <hr>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>

<script>
    import createHttp from "@/services/http";
    import utils from "../common/utils";

    export default {
        name: "UserInformation",
        data() {
            return {
                allUsers: [],
                displayedUsers: [],
                name: "",
                email: "",
                http: createHttp(),
            };
        },
        //Retrieves all users when mounted
       async mounted() {
            this.load();
        },
        methods: {
            //Retrieves all users
            load() {
                this.http.get(utils.apiUrl + "/api/v1/users/").then(response => {
                    this.allUsers = response.data;
                    this.displayedUsers = response.data;
                }).catch(err => {
                    console.log(err)
                });
            },

            //Method for searching users by their name or email
            search() {
                this.displayedUsers = [];
                //Sets points that tells what input has data
                let points = this.checkInputFields();

                //If no input fields has data, display all users
                if (points === 0) {
                    this.displayedUsers = this.allUsers;
                }
                //If only name input field has data, display users with matching first or lastname
                else if (points === 1) {
                    for (let user of this.allUsers) {
                        let fullName = user.firstname + " " + user.lastname
                        if (fullName.toLowerCase().includes(this.name.toLowerCase())){
                            this.displayedUsers.push(user);
                        }
                    }
                }
                //If only email input field has data, display users with matching email
                else if (points === 3) {
                    for (let user of this.allUsers) {
                        if (user.email.toLowerCase().includes(this.email.toLowerCase())){
                            this.displayedUsers.push(user);
                        }
                    }
                }
                //If both name and email input fields has data, display users with matching first and last name
                // as well as email
                else if (points === 4) {
                    for (let user of this.allUsers) {
                        if (user.name.toLowerCase().includes(this.name.toLowerCase())
                        && user.email.toLowerCase().includes(this.email.toLowerCase())){
                            this.displayedUsers.push(user);
                        }
                    }
                }
            },
            //Prompt asking if admin is sure of deleting user or not. Calls deleteUser() if confirmed
            deletePrompt(user){
                if (confirm("Er du sikker på at du vil slette brukeren til " + user.firstname + " " + user.lastname + "?")) {
                    this.deleteUser(user.id)
                }
            },

            //Method for deleting a user
            deleteUser(id) {
                this.http.delete(utils.apiUrl + "/api/v1/users/" + id).then(res => {
                    console.log(res);
                    //Reloads component after success
                    this.load();
                }).catch(err => {
                    console.log(err)
                });
            },

            //Method for checking which input fields has data
            //name input = 1 point and email = 3. This way one can easily check what input has data and not
            checkInputFields() {
                let points = 0;
                if (this.name && this.name != " ") {
                    points += 1;
                }
                if (this.email) {
                    points += 3;
                }
                return points
            }
        }
    }
</script>

<style scoped>
    ul {
        list-style-type: none;
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
    .inputs {
        float: left;
        display: flex;
        flex-direction: row;
        margin-top: 20px;
        margin-left: 20px;
        margin-right: 100px;
    }
    .btn {
        position: relative;
        display: flex;
        float: right;
        margin-right: 20px;
        top: 20px;
    }
    #userList {
        width: 100%;
    }
    .userInfo {
        position: relative;
        display: grid;
        grid-template-columns: 31% 31% 31% 6%;
    }
    .list hr {
        position: relative;
        margin-left: -40px;
    }
    .name {
        position: relative;
        grid-column: 1;
    }
    .email {
        position: relative;
        grid-column: 2;
    }
    .phoneNumber {
        position: relative;
        grid-column: 3;
    }
    #deleteButton {
        position: relative;
        grid-column: 4;
        color: red;
    }
    #deleteButton:hover {
        cursor: pointer;
    }
</style>