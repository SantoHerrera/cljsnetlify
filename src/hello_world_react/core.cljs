(ns hello-world-react.core
  (:require [reagent.core :as r]
            [clojure.pprint]))

;; NOTE defonce is required so that hot reloading w/ figwheel doesn't reset the state
(defonce todos (r/atom []))

(defn add-todo [text]
  (swap! todos conj {:text text :done? false}))

(defn vec-remove
  "remove elem in coll"
  [coll pos]
  (vec (concat (subvec coll 0 pos)
               (subvec coll (inc pos)))))

;; NOTE ditto for the state initialization process
(defonce init
  (do (add-todo "this is a fucking test")
      (add-todo "please work")))

;; -------------------------
;; Views

(def ^:const +return-key+ 13)

(defn todo-item [id {:keys [text done?]}]
  (let [html-id  (str "todo" id)]
    [:tr
     [:td>input {:type :checkbox
                 :checked done?
                 :id html-id
                 :on-change #(swap! todos update-in [id :done?] not)}]
     [:td>label {:for html-id} text]
     [:td>a {:href "#"
             :on-click #(swap! todos vec-remove id)
             :style {:margin-left 7}}
      "🗑"]]))

(defn form-todo []
  [:div.form-todo
   [:label {:for 'text-input} "Add a todo: "]
   [:input#text-input {:on-key-down #(when (= (.-which %) +return-key+)
                                       (let [ti (.getElementById js/document "text-input")]
                                         (add-todo (.-value ti))
                                         (set! (.-value ti) "")))}]])

(defn home-page []
  [:div
   [:h1 "TODO"]
   [:table>tbody
    (map todo-item (iterate inc 0) @todos)]
   (form-todo)
   [:h2 "Raw state content"]
   [:pre (with-out-str
           (clojure.pprint/pprint @todos))]])

;; -------------------------
;; Initialize app

(defn init! []
  (r/render [home-page] (.getElementById js/document "app")))
