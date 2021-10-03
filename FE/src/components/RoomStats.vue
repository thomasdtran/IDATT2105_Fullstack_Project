<template>
    <div id="container">
        
        <div class="dataUI">

            <h3>Antall timer hvert rom er blitt reservert:</h3>
            <div id="chart"></div>

            <b>Tidsramme:</b> <select id="timeSelect" ref="timeSelect" @change="getAllRooms">
                <option value="0">Siste uke</option>
                <option value="1">Siste måned</option>
                <option value="2">Dette semesteret</option>
            </select>
            <spinner v-show="isGettingAllRooms" />

        </div>
        
        <div class="dataUI">
            <h3>Reservasjoner i seksjoner til et rom:</h3>
            <div id="chart2"></div>
            <b>Rom:</b> <select id= "roomSelect" ref="roomSelect" @change="changeRoom">
                <option
                    v-for="room in roomNameArray"
                    v-bind:key="room"
                    v-bind:value="room"
                >
                    {{room}}
                </option>
            </select>
            <spinner v-show="isGettingAllSections" />
        </div>
    </div>
</template>

<script>
import Spinner from "@/components/Spinner.vue"
import createHttp from "@/services/http";
import { Chart } from "frappe-charts/dist/frappe-charts.esm.js";
import "frappe-charts/dist/frappe-charts.min.css";
import moment from 'moment';

export default {
    components: {
        Spinner
    },
    data(){
        return {
            http: createHttp(),
            chart: null,
            chart2: null,
            allRooms: {},
            roomNameArray: [],
            isGettingAllRooms: false,
            isGettingAllSections: false,
        }
    },
    methods: {
        /**
         * Metode for å velge hvilket rom man vil ha statistikk for.
         * Tegner et søylediagram som viser hvor mange ganger hver seksjon har blitt reservert.
         * Dersom en seksjon ikke har blitt reservert vises den ikke.
         * Henter kun data fra reservasjoner som har skjedd.
         */
        async changeRoom(){
            this.isGettingAllSections = true;

            let selectedRoom = this.$refs.roomSelect.value;
            let room = this.allRooms.find(r => r.name === selectedRoom);
            
            let sectionKeys;
            let sectionValues = [];

            await this.http.get(process.env.VUE_APP_API_URL + "/api/v1/stats/alltime/"+room.id)
            .then(res=>{
                for(let v in res.data) sectionValues.push(res.data[v]);
                sectionKeys = Object.keys(res.data);
            });
            
            let sectionNames = [];
            for (let i = 0; i < sectionKeys.length; i++) {
                sectionNames.push(room.sections.find(s => s.id === parseInt(sectionKeys[i])).name);
            }
            
            this.chart2 = new Chart("#chart2", {
                data: {
                    labels: sectionNames,
                    datasets: [
                        {
                            name: '',
                            chartType: 'bar',
                            values: sectionValues
                        }
                    ]
                },
                title: "Antall ganger seksjon er reservert",
                type: "bar",
                height: 500,
                colors: ['#0055b7'],
                axisOptions: {
                    xAxisMode: "tick",
                    xIsSeries: true
                },
                barOptions: {
                    stacked: false,
                    spaceRatio: 0.2
                },
                tooltipOptions: {
                    formatTooltipX: (d) => (d + "").toUpperCase(),
                    formatTooltipY: (d) => (d + " reservasjoner"),
                }
            });
            this.isGettingAllSections = false
        },
        /**
         * Metode for å finne statistikk på hvor mange timer hvert rom har vært i bruk/reservert.
         * Tegner et søylediagram som viser total antall tid på hvert rom oppgitt i timer.
         * Henter kun data fra reservasjoner som har skjedd.
         */
        async getAllRooms(){
            this.isGettingAllRooms = true;

            let time = parseInt(this.$refs.timeSelect.value);
            let numweeks;

            if(time === 0) numweeks = 1;
            else if(time === 1) numweeks = 4;
            else numweeks = this.findSemesterWeeks;

            let rnames = [];
            let rvalues = [];

            for await (let e of this.allRooms) {
                await this.http.get(process.env.VUE_APP_API_URL + "/api/v1/stats/"+e.id+"/"+numweeks)
                .then(res =>{ 
                    if(JSON.stringify(res.data) !== JSON.stringify({})){
                        rnames.push(Object.keys(res.data));
                        rvalues.push(res.data[Object.keys(res.data)[0]] / 60);
                    }
                });
            }

            this.chart = new Chart("#chart", {
                data: {
                    labels: rnames,
                    datasets: [
                        {
                            name: '',
                            chartType: 'bar',
                            values: rvalues
                        }
                    ]
                },
                title: "Reservert tid",
                type: "bar",
                height: 500,
                colors: ['#0055b7'],
                axisOptions: {
                    xAxisMode: "tick",
                    xIsSeries: true
                },
                barOptions: {
                    stacked: false,
                    spaceRatio: 0.2
                },
                tooltipOptions: {
                    formatTooltipX: (d) => (d + "").toUpperCase(),
                    formatTooltipY: (d) => (d + " timer reservert totalt"),
                }
            });
            this.isGettingAllRooms = false;
        },
    },
    computed: {
        /**
         * Metode for å finne ut av hvor mange uker siden semesterstart.
         * Bruker datoer hentet fra ntnu sine nettsider:
         * vårsemesteret starter 11.januar, høstsemesteret (immatrikulering) starter 16. august i
         */
        findSemesterWeeks:function(){
            let today = moment();
            let thisyear = today.year();
            let springSemesterStart = moment([thisyear, 0, 11])
            let fallSemesterStart = moment([thisyear, 7, 16])

            if(today.diff(fallSemesterStart) < 0){ //vårsemester, inkluderer sommerferien
                return today.diff(springSemesterStart, 'weeks')
            }
            else { //høstsemester
                return today.diff(fallSemesterStart, 'weeks');
            }
        }
    },
    /**
     * Henter data om alle rom til bruk senere,
     * kaller også på metoder for å tegne søylediagrammene
     */
    async mounted() {
        await this.http.get(process.env.VUE_APP_API_URL + "/api/v1/rooms")
        .then(res => {
            res.data.forEach(element => {
                this.roomNameArray.push(element.name);
            });
            this.allRooms  = res.data
        })
        this.getAllRooms();
        this.changeRoom();
    }
}
</script>

<style scoped>

*{
    box-sizing: border-box;
    margin: 0;
    padding: 0;
}

#container{
    min-height: 62vh;
}

h3{
    margin-bottom: 2rem;
}

.dataUI {
    width: 62%;
    margin: auto;
    margin-bottom: 5rem;
}

#dataUI b{
    padding-left: 1rem;
}

select {
    padding: 7px;
    margin: 10px;
}

#chart, #chart2{
    background-color: #f2f2f2;
    border-radius: 5px;
    padding: 3rem;
}

</style>
