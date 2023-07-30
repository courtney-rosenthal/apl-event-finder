<script setup>
import { ref } from 'vue';
import dayjs from "dayjs";

import Panel from "primevue/panel";
import Fieldset from 'primevue/fieldset';
import Checkbox from 'primevue/checkbox';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import Card from 'primevue/card';

const days = [
  {key: "MON", label: "Mon"},
  {key: "TUE", label: "Tue"},
  {key: "WED", label: "Wed"},
  {key: "THU", label: "Thu"},
  {key: "FRI", label: "Fri"},
  {key: "SAT", label: "Sat"},
  {key: "WEEKDAY", label: "Weekday"},
  {key: "WEEKEND", label: "Weekend"},
];


const times = [
  {key: "MORNING", label: "Morning"},
  {key: "AFTERNOON", label: "Afternoon"},
  {key: "EVENING", label: "Evening"}
];

const locations = [
  {key: "ACB", label: "Carver"},
  {key: "ACE", label: "Central"},
  {key: "ACP", label: "Cepeda"},
  {key: "AOK", label: "Hampton at Oak Hill"},
  {key: "AHC", label: "History Center"},
  {key: "AHO", label: "Howson"},
  {key: "ALW", label: "Little Walnut"},
  {key: "AMR", label: "Manchaca Road"},
  {key: "AMI", label: "Milwood"},
  {key: "ANV", label: "North Village"},
  {key: "AOQ", label: "Old Quarry"},
  {key: "APH", label: "Pleasant Hill"},
  {key: "ARR", label: "Recycled Reads"},
  {key: "ARZ", label: "Ruiz"},
  {key: "ASE", label: "Southeast"},
  {key: "ASR", label: "Spicewood Springs"},
  {key: "ASJ", label: "St. John"},
  {key: "ATB", label: "Terrazas"},
  {key: "ATO", label: "Twin Oaks"},
  {key: "AUH", label: "University Hills"},
  {key: "AWK", label: "Willie Mae Kirk"},
  {key: "AWP", label: "Windsor Park"},
  {key: "AYB", label: "Yarborough"},
  {key: "OTHER", label: "Other Locations"}
];

const ages = [
  {key: "INFANT", label: "Infant (1 year and below)"},
  {key: "TODDLER", label: "Toddler (ages 1-3)"},
  {key: "PRESCHOOLER", label: "Preschooler (ages 3-5)"},
  {key: "CHILD", label: "Child (ages 6-8)"},
  {key: "PRETEEN", label: "Preteen (ages 9-11)"},
  {key: "YOUNG_TEEN", label: "Young Teen (ages 12-14)"},
  {key: "TEEN", label: "Teen (ages 15-17)"},
  {key: "YOUNG_ADULT", label: "Young Adult (ages 18-21)"},
  {key: "ADULT", label: "Adult (ages 21 and up)"}
];

const criteria = ref({
  days: [],
  times: [],
  locations: [],
  ages: [],
  tags: [],
  searchText: null
});

function submit() {
  const request = {
    method: "POST",
    body: JSON.stringify(criteria.value),
    headers: {
      "Content-type": "application/json; charset=UTF-8"
    }
  };

  fetch("http://localhost:8080/api/calendarEvent/search", request)
    .then((response) => {
      if (!response.ok) {
        const error = new Error(response.statusText);
        error.json = response.json();
        throw error;
      }
      return response.json()
    }).then((content) => {
      console.log(content);
      searchResults.value = content;
    }).catch((ex) => {
      alert(ex)
    })
}

const searchResults = ref([])

const dayNames = [
    "Sun",
    "Mon",
    "Tue",
    "Wed",
    "Thu",
    "Fri",
    "Sat"
]

function formatDate(t) {
  return dayNames[t.day()] + ", " + t.format("MMM D");
}

function formatTime(time) {
  const t0 = dayjs.unix(parseInt(time.start));
  const t0_date = formatDate(t0);
  const t0_time = t0.format("h:mm a");
  let start = `${t0_date}, ${t0_time}`

  if (! time.end) {
    return start;
  }

  const t1 = dayjs.unix(parseInt(time.end));
  const t1_date = formatDate(t1);
  const t1_time = t1.format("h:mm a");

  if (t0_date == t1_date) {
    return `${start} - ${t1_time}`;
  } else {
    return `${start} - ${t1_date}, ${t1_time}`;
  }
}

function foob(thingy) {
  alert(thingy);
}

</script>

<template>
  <Panel header="Search for ...">

    <Fieldset legend="Day">
      <span v-for="day in days" :key="day.key">
        <Checkbox name="criteria.days" v-model="criteria.days" :inputId="day.key" :value="day.key" /><label :for="day.key">{{ day.label }}</label>
      </span>
      <br />
      <Button class="clear" label="clear choices" link @click="() => {criteria.days = [];}" />
    </Fieldset>

    <Fieldset legend="Time">
      <span v-for="time in times" :key="time.key">
        <Checkbox name="criteria.times" v-model="criteria.times" :inputId="time.key" :value="time.key" /><label :for="time.key">{{ time.label }}</label>
      </span>
      <br />
      <Button class="clear" label="clear choices" link @click="() => {criteria.times = [];}" />
    </Fieldset>

    <Fieldset legend="Location">
      <span v-for="location in locations" :key="location.key">
        <Checkbox name="criteria.locations" v-model="criteria.locations" :inputId="location.key" :value="location.key" /><label :for="location.key">{{ location.label }}</label>
      </span>
      <br />
      <Button class="clear" label="clear choices" link @click="() => {criteria.locations = [];}" />
    </Fieldset>

    <Fieldset legend="Age">
      <span v-for="age in ages" :key="age.key">
        <Checkbox name="criteria.ages" v-model="criteria.ages" :inputId="age.key" :value="age.key" /><label :for="age.key">{{ age.label }}</label>
      </span>
      <br />
      <Button class="clear" label="clear choices" link @click="() => {criteria.ages = [];}" />
    </Fieldset>

    <Fieldset legend="Tags">
      <!-- criteria.tags-->
      Picker widget for tags goes here.
    </Fieldset>


    <Fieldset legend="Words or phrases to find">
      <InputText type="text" v-model="criteria.searchText" />
    </Fieldset>

    <div>
    <Button label="Search!" @click="submit" />
    </div>

  </Panel>

  <Panel header="Events found ...">
    <div v-for="item in searchResults">
      <Card>
        <template #title>
          <a :href="item.url">{{ item.title }}</a>
        </template>
        <template #content>
          <p>{{ item.summary }}</p>
          <p>{{ item.location }}</p>
          <p>{{ formatTime(item.time) }}</p>
        </template>
      </Card>
    </div>
  </Panel>

</template>

<style scoped>
fieldset { display: block; width: 100%; clear: both; }
fieldset span { white-space: nowrap; }
label { padding-left: 5px; padding-right: 1.0em; }
.p-card div.p-card-content { padding: 0;  }  /*this isn't working*/
.clear { break-before: left; } /*this isn't working*/
</style>
