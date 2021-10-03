<!-- Admin home page where all components are added -->
<template>
    <div id="container">
        <div id="menu">
            <p class="linkPressed" v-if="pressed[0]">Brukere</p>
            <p class="link" v-if="!pressed[0]" @click="changePage(0)">Brukere</p>
            <p class="linkPressed" v-if="pressed[1]">Legg til bruker</p>
            <p class="link" v-if="!pressed[1]" @click="changePage(1)">Legg til bruker</p>
            <p class="linkPressed" v-if="pressed[2]">Legg til rom</p>
            <p class="link" v-if="!pressed[2]" @click="changePage(2)">Legg til rom</p>
            <p class="linkPressed" v-if="pressed[3]">Rediger rom</p>
            <p class="link" v-if="!pressed[3]" @click="changePage(3)">Rediger rom</p>
            <p class="linkPressed" v-if="pressed[4]">Endre reservasjoner</p>
            <p class="link" v-if="!pressed[4]" @click="changePage(4)">Endre reservasjoner</p>

            <p class="link" @click="logOut">Logg ut</p>
        </div>
        <UserInformation v-if="page === 0"></UserInformation>
        <AddUser v-if="page === 1"></AddUser>
        <AddRoom v-if="page === 2"></AddRoom>
        <EditRooms v-if="page === 3"></EditRooms>
        <EditReservation v-if="page === 4"></EditReservation>
    </div>
</template>

<script>
    import UserInformation from "../components/UserInformation";
    import AddUser from "../components/AddUser";
    import AddRoom from "../components/AddRoom";
    import EditReservation from "../components/EditReservation";
    import EditRooms from "../components/EditRooms";
    import store from "../store";
    export default {
        name: "AdminPage",
        components: {EditRooms, EditReservation, AddRoom, AddUser, UserInformation},
        data() {
            return {
                page: 0,
                pressed: [true, false, false, false, false],
            };
        },
        methods: {
            //Method for changing displayed component on admin home site
            changePage(pageNumber) {
                this.pressed[this.page] = false;
                this.pressed[pageNumber] = true;
                this.page = pageNumber;
            },
            //Method for logging out of the admin site
            logOut() {
                store.dispatch("adminLogout");
            },
        }
    }
</script>

<style scoped>


    #container {
        position: relative;
        width: 100%;
    }
    #menu {
        position: relative;
        float: left;
        left: 22.4%;
        z-index: 3;
        margin-top:1rem;
        border-bottom: 3px solid #f2f2f2;
    }
    #menuTxt {
        float: left;
        margin-left: 20px;
        margin-right: 20px;
    }
    .linkPressed {
        float: left;
        margin-left: 20px;
        margin-right: 20px;
        background-color: #d8e1e8;
        padding: 0.3rem;
        border-radius: 4px;
    }
    .link {
        float: left;
        color: black;
        margin-left: 20px;
        margin-right: 20px;
    }
    .link:hover {
        cursor: pointer;
    }

</style>