/** @todo Generate this file using the backend Swagger definition */

export interface SearchResults {
  currPage: number;
  firstPage: number;
  firstResult: number;
  lastPage: number;
  lastResult: number;
  nextPage: number;
  numPages: number;
  numResults: number;
  prevPage: number | null;
  results: SearchResult[];
  resultsPerPage: number;
}

export interface SearchResult {
  content: string;
  description: string;
  isDeleted: boolean;
  isFree: boolean;
  location: { detail: string; key: string };
  recommendedAge: { maxYears: number | null; minYears: number | null } | null;
  registrationUrl: string | null;
  subTitle: string | null;
  summary: string;
  tags: string[];
  time: Time;
  timestamp: number;
  title: string;
  url: string;
}

export interface Time {
  start: number;
  end: number;
  localDayOfWeek: string;
  localHourOfDay: number;
}

export type SearchDay =
  | "MON"
  | "TUE"
  | "WED"
  | "THU"
  | "FRI"
  | "SAT"
  | "WEEKDAY"
  | "WEEKEND";

export type SearchTime = "MORNING" | "AFTERNOON" | "EVENING";

export type SearchLocation =
  | "ACB"
  | "ACE"
  | "ACP"
  | "AOK"
  | "AHC"
  | "AHO"
  | "ALW"
  | "AMR"
  | "AMI"
  | "ANV"
  | "AOQ"
  | "APH"
  | "ARR"
  | "ARZ"
  | "ASE"
  | "ASR"
  | "ASJ"
  | "ATB"
  | "ATO"
  | "AUH"
  | "AWK"
  | "AWP"
  | "AYB";

export type SearchAge =
  | "INFANT"
  | "TODDLER"
  | "PRESCHOOLER"
  | "CHILD"
  | "PRETEEN"
  | "YOUNG_TEEN"
  | "TEEN"
  | "YOUNG_ADULT"
  | "ADULT"
  | null;

export interface SearchCriteria {
  days: SearchDay[];
  times: SearchTime[];
  locations: SearchLocation[];
  age: SearchAge;
  tags: string[];
  searchText: string | null;
}
