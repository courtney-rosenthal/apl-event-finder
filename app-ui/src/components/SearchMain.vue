<script setup>
import {ref} from 'vue';

import Button from 'primevue/button';
import Checkbox from 'primevue/checkbox';
import Fieldset from 'primevue/fieldset';
import InputText from 'primevue/inputtext';
import Listbox from 'primevue/listbox';
import Panel from 'primevue/panel';
import SelectButton from 'primevue/selectbutton';

import SearchResults from './SearchResults.vue';

// Base URL of the backend API
const BASE_URL = import.meta.env.VITE_API_BASE_URL


/*
 * Perform a call to the backend service. Catches errors, returns content body.
 */
async function retrieveFromBackend(url, request) {
  try {
    const response = await fetch(BASE_URL + url, request);
    if (response.ok) {
      return response.json();
    }
    const error = new Error(response.statusText);
    error.json = response.json();
    throw error;
  } catch (error) {
    alert("Fetch from backend service failed.\n\n" + error);
    throw error;
  }
}

// The search criteria being selected
const searchCriteria = ref({
  days: [],
  times: [],
  locations: [],
  age: null,
  tags: [],
  searchText: null
});

// Snapshot of the search criteria, at the time the search is initiated
// This is done so we can re-use the criteria to paginate through the results
let currentSearchCriteria = {};

// The results of the most recent search query
const searchResults = ref({});

/**
 * Action performed when search is requested.
 */
function performSearch() {
  // Snapshot the current search criteria.
  currentSearchCriteria = { ...searchCriteria.value };

  // Go do a search, with the snapshotted criteria.
  goToResultsPage(0);
}

/**
 * Action performed to paginate through results for current search.
 */
async function goToResultsPage(pageNum) {
  console.log("currentSearchCriteria = ", currentSearchCriteria);
  const request = {
    method: "POST",
    body: JSON.stringify(currentSearchCriteria),
    headers: {
      "Content-type": "application/json; charset=UTF-8"
    }
  };
  const response = await retrieveFromBackend(`/calendarEvent/search?pageNum=${pageNum}`, request);
  searchResults.value = response;
}


/*
 * Action performed when search criteria reset is requested.
 */
function resetSearchCriteria(whut) {
  if (whut == "days" || whut == "all") {
    searchCriteria.value.days = [];
  }
  if (whut == "times" || whut == "all") {
    searchCriteria.value.times = [];
  }
  if (whut == "locations" || whut == "all") {
    searchCriteria.value.locations = [];
  }
  if (whut == "age" || whut == "all") {
    searchCriteria.value.ages = null;
  }
  if (whut == "tags" || whut == "all") {
    searchCriteria.value.tags = [];
  }
  if (whut == "all") {
    searchCriteria.value.searchText = null;
  }
}


/*
 * On startup retrieve list of tags used.
 */
const allTags = ref([]);
async function loadTags() {
  const response = await retrieveFromBackend("/calendarEvent/tags", {method: "GET"});
  allTags.value = response;
}
loadTags();


/*
 * TODO - These should be moved to the backend service, and fetched from there.
 */
const criteriaSelections = {
  days: [
    {key: "MON", label: "Mon"},
    {key: "TUE", label: "Tue"},
    {key: "WED", label: "Wed"},
    {key: "THU", label: "Thu"},
    {key: "FRI", label: "Fri"},
    {key: "SAT", label: "Sat"},
    {key: "WEEKDAY", label: "Weekday"},
    {key: "WEEKEND", label: "Weekend"},
  ],
  times: [
    {key: "MORNING", label: "Morning"},
    {key: "AFTERNOON", label: "Afternoon"},
    {key: "EVENING", label: "Evening"}
  ],
  locations: [
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
    {key: "AYB", label: "Yarborough"}
  ],
  ages: [
    {key: "INFANT", label: "Infant (1 year and below)"},
    {key: "TODDLER", label: "Toddler (ages 1-3)"},
    {key: "PRESCHOOLER", label: "Preschooler (ages 3-5)"},
    {key: "CHILD", label: "Child (ages 6-8)"},
    {key: "PRETEEN", label: "Preteen (ages 9-11)"},
    {key: "YOUNG_TEEN", label: "Young Teen (ages 12-14)"},
    {key: "TEEN", label: "Teen (ages 15-17)"},
    {key: "YOUNG_ADULT", label: "Young Adult (ages 18-21)"},
    {key: "ADULT", label: "Adult (ages 21 and up)"},
    {key: null, label: "All ages"}
  ]
}

</script>

<template>
  <Panel header="Search for ...">

    <Fieldset legend="Day">
      <span v-for="day in criteriaSelections.days" :key="day.key">
        <Checkbox name="criteria.days" v-model="searchCriteria.days" :inputId="day.key" :value="day.key" /><label :for="day.key">{{ day.label }}</label>
      </span>
      <br />
      <Button class="clear" label="clear choices" link @click="resetSearchCriteria('days')" />
    </Fieldset>

    <Fieldset legend="Time">
      <span v-for="time in criteriaSelections.times" :key="time.key">
        <Checkbox name="criteria.times" v-model="searchCriteria.times" :inputId="time.key" :value="time.key" /><label :for="time.key">{{ time.label }}</label>
      </span>
      <br />
      <Button class="clear" label="clear choices" link @click="resetSearchCriteria('times')" />
    </Fieldset>

    <Fieldset legend="Location">
      <span v-for="location in criteriaSelections.locations" :key="location.key">
        <Checkbox name="criteria.locations" v-model="searchCriteria.locations" :inputId="location.key" :value="location.key" /><label :for="location.key">{{ location.label }}</label>
      </span>
      <br />
      <Button class="clear" label="clear choices" link @click="resetSearchCriteria('locations')" />
    </Fieldset>

    <Fieldset legend="Age">
      <i class="pi pi-exclamation-triangle" style="color: black; background: yellow; padding: 3px" title='Warning! The "age" criteria currently is not reliable, due to source data issues.'></i>
      <SelectButton v-model="searchCriteria.age" :options="criteriaSelections.ages" optionLabel="label" optionValue="key" aria-labelledby="basic" />
<!--      <Button class="clear" label="clear choices" link @click="resetSearchCriteria('age')" />-->
    </Fieldset>

    <Fieldset legend="Tags">
      <Listbox name="criterial.tags" v-model="searchCriteria.tags" :options="allTags" multiple
               :virtualScrollerOptions="{ itemSize: 38 }" listStyle="height:250px" />
      <Button class="clear" label="clear choices" link @click="resetSearchCriteria('tags')" />
    </Fieldset>

    <Fieldset legend="Words or phrases to find">
      <InputText type="text" v-model="searchCriteria.searchText" />
    </Fieldset>

    <div>
      <Button label="Search!" @click="performSearch()" />
      <Button class="clear" label="clear all choices" link @click="resetSearchCriteria('all')" />
    </div>

  </Panel>

  <Panel header="Events found ...">
    <SearchResults :searchResults="searchResults" @go-to-results-page="(n) => goToResultsPage(n)" />
  </Panel>

</template>

<style scoped>
fieldset { display: block; width: 100%; clear: both; }
fieldset span { white-space: nowrap; }
label { padding-left: 5px; padding-right: 1.0em; }
.p-card div.p-card-content { padding: 0;  }  /*this isn't working*/
.clear { break-before: left; } /*this isn't working*/
</style>
