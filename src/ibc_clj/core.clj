 (ns ibc-clj.core 
  (:gen-class)
  (:require  [compojure.core :refer [defroutes GET POST DELETE PUT ]]
             [compojure.route :as route]
             [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
             [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
             [ring.middleware.cors :refer [wrap-cors]]
             [cheshire.core :as json]
             [next.jdbc :as jdbc]
             [next.jdbc.sql :as sql]))
   
;;-----------------------------------------------------------------
;;-- Configuração do banco de dados  ------------------------------
;;-----------------------------------------------------------------
;;(def db-spec {:dbtype "mysql"
;;              :dbname "ibc_localhost"
;;              :host "localhost"
;;             :port 3307
;;              :user "root"
;;              :password "maua"})

(def db-spec {:dbtype "mysql"
              :dbname "qualitsys_ibc_desenv"
              :host "localhost"
              :port 3306
              :user "qualitsys_ibc_dev"
              :password "Ub{+hdAUeC$m"})

(def ds (jdbc/get-datasource db-spec))

;;-----------------------------------------------------------------
;;-- Handlers p/ contact_types ------------------------------------
;;----------------------------------------------------------------- 
;; 
(defn get-contact-type-handler [request]
  (let [id (Integer/parseInt (get-in request [:params :id]))
        result (sql/find-by-keys ds :contact_types {:id id})]
    (if (seq result)
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string (first result))}
      {:status 404
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string {:error "Contact type não encontrado."})})))

;----------------------------------------------------------------- 
(defn list-contact-types-handler [] 
  (let [result (sql/query ds ["SELECT * FROM contact_types"])]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string result)}))

;;----------------------------------------------------------------- 
(defn insert-contact-type-handler [request]
  (let [params (:body request)
        id (Integer. (get params "id"))
        description (get params "description")
        priority (get params "priority")
        status (get params "status")
        created_at (java.time.LocalDateTime/now)
        
        ;;created_by (Integer. (get params "created_by"))
        created_by (get params "created_by") 

        updated_at (java.time.LocalDateTime/now)
        
        ;;updated_by (Integer. (get params "updated_by"))
        updated_by (get params "updated_by")]
    
   (try
        (sql/insert! ds :contact_types
                    {:id id
                     :description description
                     :priority priority
                     :status status
                     :created_at created_at
                     :created_by created_by
                     :updated_at updated_at
                     :updated_by updated_by})
      
      {:status 201
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string {:message "Contact-type criado com sucesso!"})}
      
      (catch Exception e
        {:status 500
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:error (str "Erro ao inserir um Contact-type: " (.getMessage e))})}))))

;;----------------------------------------------------------------- 
(defn delete-contact-type-handler [request]
  (let [params (:body request)
        id (Integer. (get params "id"))]

   (try
     (let [existing (sql/get-by-id ds :contact_types id)]
       (if existing
         (do

           (sql/delete! ds :contact_types ["id = ?" id])
           {:status 200
            :headers {"Content-Type" "application/json"}
            :body (json/generate-string {:message "Contact-type deletado com sucesso!"})})

           {:status 404
            :headers {"Content-Type" "application/json"}
            :body (json/generate-string {:error "Contact-type não encontrado."})}))

     (catch Exception e
       {:status 500
        :headers {"Content-Type" "application/json"}
        :body (json/generate-string {:error (str "Erro ao deletar o Contact-type: " (.getMessage e))})}))))

;;----------------------------------------------------------------- 
(defn update-contact-type-handler [request]
  (let [params (:body request)
        id (Integer. (get params "id"))
        description (get params "description")
        priority (get params "priority")
        status (get params "status")
        updated_at (java.time.LocalDateTime/now)
        updated_by (get params "updated_by")]
    (try
      (let [existing (sql/find-by-keys ds :contact_types {:id id})]
        (if (seq existing)
          (do
            (sql/update! ds :contact_types
                         {:description description
                          :priority priority
                          :status status
                          :updated_at updated_at
                          :updated_by updated_by}
                         ["id = ?" id])
            {:status 200
             :headers {"Content-Type" "application/json"}
             :body (json/generate-string {:message "Contact-type atualizado com sucesso!"})})
          {:status 404
           :headers {"Content-Type" "application/json"}
           :body (json/generate-string {:error "Contact-type não encontrado."})}))
      (catch Exception e
        {:status 500
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:error (str "Erro ao atualizar o Contact-type: " (.getMessage e))})}))))

;;-----------------------------------------------------------------
;;-- Handlers p/ prayer_request_types  ----------------------------
;;----------------------------------------------------------------- 
;; 
(defn get-prayer-request-types-handler [request]
  (let [id (Integer/parseInt (get-in request [:params :id]))
        result (sql/find-by-keys ds :prayer_request_types {:id id})]
    (if (seq result)
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string (first result))}
      {:status 404
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string {:error "Prayer Request Type não encontrado."})})))

;----------------------------------------------------------------- 
(defn list-prayer-request-types-handler [] 
  (let [result (sql/query ds ["SELECT * FROM prayer_request_types"])]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string result)}))

;----------------------------------------------------------------- 
(defn insert-prayer-request-type-handler [request]
  
  (let [params (:body request)
        id (Integer. (get params "id"))
        description (get params "description")
        created_at (java.time.LocalDateTime/now)

        ;;created_by (Integer. (get params "created_by"))
        created_by (get params "created_by")

        updated_at (java.time.LocalDateTime/now)

        ;;updated_by (Integer. (get params "updated_by"))
        updated_by (get params "updated_by")]

    (try
      (sql/insert! ds :prayer_request_types
                   {:id id
                    :description description
                    :created_at created_at
                    :created_by created_by
                    :updated_at updated_at
                    :updated_by updated_by})

      {:status 201
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string {:message "Prayer Request Type criado com sucesso!"})}

      (catch Exception e
        {:status 500
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:error (str "Erro ao inserir um Prayer-request-type: " (.getMessage e))})}))))

;;----------------------------------------------------------------- 
(defn delete-prayer-request-type-handler [request]
  (let [params (:body request)
        id (Integer. (get params "id"))]

   (try
     (let [existing (sql/get-by-id ds :prayer_request_types id)]
       (if existing
         (do

           (sql/delete! ds :prayer_request_types ["id = ?" id])
           {:status 200
            :headers {"Content-Type" "application/json"}
            :body (json/generate-string {:message "Prayer Request-type deletado com sucesso!"})})

           {:status 404
            :headers {"Content-Type" "application/json"}
            :body (json/generate-string {:error "Prayer Request-type não encontrado."})}))

     (catch Exception e
       {:status 500
        :headers {"Content-Type" "application/json"}
        :body (json/generate-string {:error (str "Erro ao deletar o Prayer-Request-type: " (.getMessage e))})}))))

;;-------------------------------------------------------------------------
(defn update-prayer-request-type-handler [request]
  (let [params (:body request)
        id (Integer. (get params "id"))
        description (get params "description")
        updated_at (java.time.LocalDateTime/now)
        updated_by (get params "updated_by")]
    (try
      (let [existing (sql/find-by-keys ds :prayer_request_types {:id id})]
        (if (seq existing)
          (do
            (sql/update! ds :prayer_request_types
                         {:description description
                          :updated_at updated_at
                          :updated_by updated_by}
                         ["id = ?" id])
            {:status 200
             :headers {"Content-Type" "application/json"}
             :body (json/generate-string {:message "Prayer Request-type atualizado com sucesso!"})})
          {:status 404
           :headers {"Content-Type" "application/json"}
           :body (json/generate-string {:error "Prayer Request-type não encontrado."})}))
      (catch Exception e
        {:status 500
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:error (str "Erro ao atualizar o Prayer Request-type: " (.getMessage e))})}))))

;;-----------------------------------------------------------------
;;-- Handlers p/ roles   ------------------------------------
;;----------------------------------------------------------------- 
;; 
(defn get-roles-handler [request]
  (let [id (Integer/parseInt (get-in request [:params :id]))
        result (sql/find-by-keys ds :roles {:id id})]
    (if (seq result)
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string (first result))}
      {:status 404
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string {:error "Role não encontrado."})})))

;----------------------------------------------------------------- 
(defn list-roles-handler [] 
  (let [result (sql/query ds ["SELECT * FROM roles"])]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string result)}))

;;----------------------------------------------------------------- 
(defn insert-role-handler [request]
  (let [params (:body request)
        id (Integer. (get params "id"))
        description (get params "description")
        created_at (java.time.LocalDateTime/now)
        
        ;;created_by (Integer. (get params "created_by"))
        created_by (get params "created_by") 

        updated_at (java.time.LocalDateTime/now)
        
        ;;updated_by (Integer. (get params "updated_by"))
        updated_by (get params "updated_by")]
    
   (try
        (sql/insert! ds :roles
                    {:id id
                     :description description
                     :created_at created_at
                     :created_by created_by
                     :updated_at updated_at
                     :updated_by updated_by})
      
      {:status 201
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string {:message "Role criado com sucesso!"})}
      
      (catch Exception e
        {:status 500
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:error (str "Erro ao inserir um Role: " (.getMessage e))})}))))

;;----------------------------------------------------------------- 
(defn delete-role-handler [request]
  (let [params (:body request)
        id (Integer. (get params "id"))]

   (try
     (let [existing (sql/get-by-id ds :roles id)]
       (if existing
         (do

           (sql/delete! ds :roles ["id = ?" id])
           {:status 200
            :headers {"Content-Type" "application/json"}
            :body (json/generate-string {:message "Role deletado com sucesso!"})})

           {:status 404
            :headers {"Content-Type" "application/json"}
            :body (json/generate-string {:error "Role não encontrado."})}))

     (catch Exception e
       {:status 500
        :headers {"Content-Type" "application/json"}
        :body (json/generate-string {:error (str "Erro ao deletar o Role: " (.getMessage e))})}))))

;;----------------------------------------------------------------- 
(defn update-role-handler [request]
  (let [params (:body request)
        id (Integer. (get params "id"))
        description (get params "description")
        updated_at (java.time.LocalDateTime/now)
        updated_by (get params "updated_by")]
    (try
      (let [existing (sql/find-by-keys ds :roles {:id id})]
        (if (seq existing)
          (do
            (sql/update! ds :roles
                         {:description description
                          :updated_at updated_at
                          :updated_by updated_by}
                         ["id = ?" id])
            {:status 200
             :headers {"Content-Type" "application/json"}
             :body (json/generate-string {:message "Role atualizado com sucesso!"})})
          {:status 404
           :headers {"Content-Type" "application/json"}
           :body (json/generate-string {:error "Role não encontrado."})}))
      (catch Exception e
        {:status 500
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:error (str "Erro ao atualizar o Role: " (.getMessage e))})}))))

;;-----------------------------------------------------------------
;;-- Handlers p/ permissions   ------------------------------------
;;----------------------------------------------------------------- 
;; 
(defn get-permissions-handler [request]
  (let [id (Integer/parseInt (get-in request [:params :id]))
        result (sql/find-by-keys ds :permissions {:id id})]
    (if (seq result)
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string (first result))}
      {:status 404
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string {:error "Permission não encontrada."})})))

;----------------------------------------------------------------- 
(defn list-permissions-handler [] 
  (let [result (sql/query ds ["SELECT * FROM permissions"])]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string result)}))

;;----------------------------------------------------------------- 
(defn insert-permission-handler [request]
  (let [params (:body request)
        id (Integer. (get params "id"))
        description (get params "description")
        created_at (java.time.LocalDateTime/now)
        
        ;;created_by (Integer. (get params "created_by"))
        created_by (get params "created_by") 

        updated_at (java.time.LocalDateTime/now)
        
        ;;updated_by (Integer. (get params "updated_by"))
        updated_by (get params "updated_by")]
    
   (try
        (sql/insert! ds :permissions
                    {:id id
                     :description description
                     :created_at created_at
                     :created_by created_by
                     :updated_at updated_at
                     :updated_by updated_by})
      
      {:status 201
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string {:message "Permission criada com sucesso!"})}
      
      (catch Exception e
        {:status 500
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:error (str "Erro ao inserir uma Permission! " (.getMessage e))})}))))

;;----------------------------------------------------------------- 
(defn delete-permission-handler [request]
  (let [params (:body request)
        id (Integer. (get params "id"))]

   (try
     (let [existing (sql/get-by-id ds :permissions id)]
       (if existing
         (do

           (sql/delete! ds :permissions ["id = ?" id])
           {:status 200
            :headers {"Content-Type" "application/json"}
            :body (json/generate-string {:message "Permission deletada com sucesso!"})})

           {:status 404
            :headers {"Content-Type" "application/json"}
            :body (json/generate-string {:error "Permission não encontrada!"})}))

     (catch Exception e
       {:status 500
        :headers {"Content-Type" "application/json"}
        :body (json/generate-string {:error (str "Erro ao deletar a Permission! " (.getMessage e))})}))))

;;----------------------------------------------------------------- 
(defn update-permission-handler [request]
  (let [params (:body request)
        id (Integer. (get params "id"))
        description (get params "description")
        updated_at (java.time.LocalDateTime/now)
        updated_by (get params "updated_by")]
    (try
      (let [existing (sql/find-by-keys ds :permissions {:id id})]
        (if (seq existing)
          (do
            (sql/update! ds :permissions
                         {:description description
                          :updated_at updated_at
                          :updated_by updated_by}
                         ["id = ?" id])
            {:status 200
             :headers {"Content-Type" "application/json"}
             :body (json/generate-string {:message "Permission atualizada com sucesso!"})})
          {:status 404
           :headers {"Content-Type" "application/json"}
           :body (json/generate-string {:error "Permission não encontrada!"})}))
      (catch Exception e
        {:status 500
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:error (str "Erro ao atualizar a Permission! " (.getMessage e))})}))))

;;---------------------------------------------------------------------
;; -- Endpoints   -----------------------------------------------------
;;---------------------------------------------------------------------
(defroutes app-routes
  ;;------------------------------------------------------------------
  ;;--- Rotas para contact_types
  ;;------------------------------------------------------------------
  (GET "/hello" [] "Hello, World")
  (GET "/greet" [] "HELLO IBC")
  (GET "/contact-type/:id" [id] (get-contact-type-handler {:params {:id id}}))
  (GET "/list-contact-types"  []  (list-contact-types-handler ))
  (POST "/ins-contact-type" request (insert-contact-type-handler request))
  (DELETE "/del-contact-type" request (delete-contact-type-handler request))
  (PUT "/upd-contact-type" request (update-contact-type-handler request)) 
 
  ;;-------------------------------------------------------------------
  ;;--- Rotas para prayer_request_types
  ;;-------------------------------------------------------------------
  (GET "/prayer-request-types/:id" [id] (get-prayer-request-types-handler {:params {:id id}}))
  (GET "/list-prayer-request-types"  []  (list-prayer-request-types-handler))
  (POST "/ins-prayer-request-type" request (insert-prayer-request-type-handler request))
  (DELETE "/del-prayer-request-type" request (delete-prayer-request-type-handler request))
  (PUT "/upd-prayer-request-type" request (update-prayer-request-type-handler request)) 

  ;;-------------------------------------------------------------------
  ;;--- Rotas para roles
  ;;-------------------------------------------------------------------
  (GET "/roles/:id" [id] (get-roles-handler {:params {:id id}}))
  (GET "/list-roles"  []  (list-roles-handler))
  (POST "/ins-role" request (insert-role-handler request))
  (DELETE "/del-role" request (delete-role-handler request))
  (PUT "/upd-role" request (update-role-handler request)) 

  ;;-------------------------------------------------------------------
  ;;--- Rotas para permissions
  ;;-------------------------------------------------------------------
  (GET "/permissions/:id" [id] (get-permissions-handler {:params {:id id}}))
  (GET "/list-permissions"  []  (list-permissions-handler))
  (POST "/ins-permission" request (insert-permission-handler request))
  (DELETE "/del-permission" request (delete-permission-handler request))
  (PUT "/upd-permission" request (update-permission-handler request)) 

  (route/not-found "Rota não definida!"))

;;---------------------------------------------------------------------
;; --- Middleware  ----------------------------------------------------
;;---------------------------------------------------------------------
(def app
  (-> (wrap-cors app-routes
       ;; :access-control-allow-origin [#"http://127.0.0.1:9999"         ;; Apache localhost
       ;;                               #"http://127.0.0.1:8080"         ;; http-server localhost
       ;;                               #"http://www.qualitsys.net.br"]  ;;Centos VPS Linux          


      :access-control-allow-origin [#"http://www.qualitsys.net.br"]       ;;Centos VPS Linux
      :access-control-allow-methods [:get :post :put :delete]
      :access-control-allow-headers ["Content-Type" "X-CSRF-Token"])
      
      (wrap-json-body {:keywords? false})
      (wrap-json-response)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))
;;---------------------------------------------------------------------
(defn -main []
  (println "Iniciando o servidor..."))
;;---------------------------------------------------------------------