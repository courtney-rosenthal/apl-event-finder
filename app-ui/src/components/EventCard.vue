<script setup>
defineProps(['item']);

import dayjs from "dayjs";
import Card from 'primevue/card';

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
</script>

<template>
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
</template>

<style scoped>
fieldset { display: block; width: 100%; clear: both; }
fieldset span { white-space: nowrap; }
label { padding-left: 5px; padding-right: 1.0em; }
.p-card div.p-card-content { padding: 0;  }  /*this isn't working*/
.clear { break-before: left; } /*this isn't working*/
</style>
