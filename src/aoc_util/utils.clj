(ns aoc-util.utils
  "Helper functions"
(:gen-class
   :name aoc_util.utils
   :prefix "-"
   :main false
   :methods [#^{:static true} [numbersFromStr [String] java.util.List]
             #^{:static true} [strInt [String] Integer]])
  (:import [java.io BufferedReader StringReader])
  (:require [clojure.edn :as edn]))

(defmacro save
  "Executes a body, if an excetption throws, 
  catch and print stacktrace to `*err*` but returns `nil`"
  [& body]
  `(try
     ~@body
     (catch Exception e#
       (binding [*out* *err*]
         (println (.getMessage e#)))
       (.printStackTrace e#))))

 (defmacro define
  "Inspired from scheme to shortcut functions which return a
  lambda function

  ```
  (define f [a] [b] (+ a b))

  ((f 5) 3) ;;=> 8
  ```"
  [name bindings lambda-bindings & body]
  (let [doc-str? (if (string? (first body))
                   (first body)
                   "define scheme inspired")
        body (if (string? (first body))
               (next body)
               body)]
    `(defn ~name ~doc-str?
       [~@bindings]
       (fn [~@lambda-bindings]
         ~@body))))

(def
  ^{:doc "Takes a string and tries to parse into an Integer, otherwise nil"
    :arglists '([^String n])}
  str->int
  (memoize
    (fn [^String n]
      (save
        (Integer/parseInt n)))))

(defn vec-remove
  "remove elem in coll"
  [pos coll]
  (into (subvec coll 0 pos) (subvec coll (inc pos))))

(defn transpose [m]
  (apply mapv vector m))

(defn -strInt [s]
  (str->int s))

(defn numbers-from-str
  "Retrieves all numbers from a string
  returns a list of numbers"
  [^String str]
  (map edn/read-string (re-seq #"\d+" str)))


(defn -numbersFromStr [^String s]
  (numbers-from-str s))

(defn line-process
  "Process a string line by line
  takes a parser fn which is used with mapv, not lazy"
  ([^String input] (line-process input identity))
  ([^String input parser]
   (when (string? input)
     (mapv parser (line-seq (BufferedReader. (StringReader. input)))))))
