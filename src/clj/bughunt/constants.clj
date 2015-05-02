(ns bughunt.constants)

(def BUG_STATUSES
  ["Confirmed"
   "Expired"
   "Fix Committed"
   "Fix Released"
   "In Progress"
   "Incomplete"
   "Invalid"
   "New"
   "Opinion"
   "Triaged"
   "Won't Fix"])

(def BUG_DATE_FIELDS
  [:date_assigned
   :date_closed
   :date_confirmed
   :date_created
   :date_fix_committed
   :date_fix_released
   :date_in_progress
   :date_incomplete
   :date_left_closed     ; date reopened
   :date_left_new        ; date marked with a status higher than New
   :date_triaged])


(def SEARCH_FIELDS
  [:target
   :milestone
   :status
   :importance
   :reporter
   :assignee
   :verifier
   :criteria
   :tags
   :exclude-tags
   :date-created-after
   :date-created-before
   :date-triaged-after
   :date-triaged-before
   :date-fix_committed-after
   :date-fix-committed-before
   :date-fix-released-after
   :date-fix-released-before])
