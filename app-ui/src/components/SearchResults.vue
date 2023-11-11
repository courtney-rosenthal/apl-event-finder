<script setup>
  defineProps(['searchResults']);

  import EventCard from "./EventCard.vue";

  function goToPage(page) {
    alert(`Sorry, pagination does not work yet.\nWhen it does, this link will go to page ${page+1}.`);
  }
</script>

<template>
  <div v-if="! searchResults.results" >
    <p style="text-align: center; margin-top: 8px">
      Results will appear here when you do a search.
    </p>
  </div>
  <div v-else-if="searchResults.results.length == 0">
    <p style="text-align: center; margin-top: 8px">
      No matching results found!
    </p>
  </div>
  <div v-else>
    <EventCard v-for="item in searchResults.results" :item="item" />
    <p style="text-align: center; margin-top: 8px">
      <span v-if="searchResults.prevPage">
        <i class="pi pi-angle-double-left pager-link-active" @click="goToPage(searchResults.firstPage)"></i>
        <i class="pi pi-angle-left pager-link-active" @click="goToPage(searchResults.prevPage)"></i>
      </span>
      <span v-else>
        <i class="pi pi-angle-double-left pager-link-inactive"></i>
        <i class="pi pi-angle-left pager-link-inactive"></i>
      </span>
      {{ searchResults.firstResult+1 }} to {{ searchResults.lastResult+1 }} of {{ searchResults.numResults }}
      <span v-if="searchResults.nextPage">
        <i class="pi pi-angle-right pager-link-active" @click="goToPage(searchResults.nextPage)"></i>
        <i class="pi pi-angle-double-right pager-link-active" @click="goToPage(searchResults.lastPage)"></i>
      </span>
      <span v-else>
        <i class="pi pi-angle-right pager-link-inactive"></i>
        <i class="pi pi-angle-double-right pager-link-inactive"></i>
      </span>
    </p>
  </div>
</template>

<style scoped>
.pager-link-active:hover {
  background: yellow;
}
.pager-link-inactive {
  color: #ccc;
}
</style>