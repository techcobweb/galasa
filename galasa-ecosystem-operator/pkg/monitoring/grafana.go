package monitoring

import (
	"strings"

	galasav1alpha1 "github.com/galasa-dev/extensions/galasa-ecosystem-operator/pkg/apis/galasa/v1alpha1"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/api/extensions/v1beta1"
	"k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
)

type Grafana struct {
	PersistentVolumeClaim  *corev1.PersistentVolumeClaim
	ConfigMap              *corev1.ConfigMap
	ProvisioningConfigMap  *corev1.ConfigMap
	DashboardConfigMap     *corev1.ConfigMap
	AutoDashboardConfigMap *corev1.ConfigMap
	Deployment             *appsv1.Deployment
	InternalService        *corev1.Service
	ExposedService         *corev1.Service
	Ingress                *v1beta1.Ingress
}

func NewGrafana(cr *galasav1alpha1.GalasaEcosystem) *Grafana {
	return &Grafana{
		InternalService:        generateGrafanaInternalService(cr),
		ExposedService:         generateGrafanaExposedService(cr),
		Deployment:             generateGrafanaDeployment(cr),
		PersistentVolumeClaim:  generateGrafanaPVC(cr),
		ConfigMap:              generateGrafanaConfig(cr),
		ProvisioningConfigMap:  generateProvisioningConfigMap(cr),
		DashboardConfigMap:     generateDashboardConfigMap(cr),
		AutoDashboardConfigMap: generateAutoDashboardConfigMap(cr),
		Ingress:                generateGrafanaIngress(cr),
	}
}

func generateGrafanaIngress(cr *galasav1alpha1.GalasaEcosystem) *v1beta1.Ingress {
	return &v1beta1.Ingress{
		ObjectMeta: metav1.ObjectMeta{
			Name:      cr.Name + "-grafana-ingress",
			Namespace: cr.Namespace,
			Labels: map[string]string{
				"app": cr.Name + "-grafana",
			},
			Annotations: map[string]string{
				"kubernetes.io/ingress.class": cr.Spec.IngressClass,
			},
		},
		Spec: v1beta1.IngressSpec{
			Rules: []v1beta1.IngressRule{
				{
					IngressRuleValue: v1beta1.IngressRuleValue{
						HTTP: &v1beta1.HTTPIngressRuleValue{
							Paths: []v1beta1.HTTPIngressPath{
								{
									Path: "/galasa-grafana",
									Backend: v1beta1.IngressBackend{
										ServiceName: cr.Name + "-grafana-external-service",
										ServicePort: intstr.FromInt(3000),
									},
								},
							},
						},
					},
				},
			},
		},
	}
}

func generateAutoDashboardConfigMap(cr *galasav1alpha1.GalasaEcosystem) *corev1.ConfigMap {
	return &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "grafana-auto-dashboard",
			Namespace: cr.Namespace,
			Labels: map[string]string{
				"app": cr.Name + "-grafana",
			},
		},
		Data: map[string]string{
			"dashboard.json": `
			{
				"annotations": {
				  "list": [
					{
					  "builtIn": 1,
					  "datasource": "-- Grafana --",
					  "enable": true,
					  "hide": true,
					  "iconColor": "rgba(0, 211, 255, 1)",
					  "name": "Annotations & Alerts",
					  "type": "dashboard"
					}
				  ]
				},
				"editable": true,
				"gnetId": null,
				"graphTooltip": 0,
				"id": 1,
				"links": [],
				"panels": [
				  {
					"aliasColors": {},
					"bars": false,
					"dashLength": 10,
					"dashes": false,
					"fill": 1,
					"gridPos": {
					  "h": 10,
					  "w": 12,
					  "x": 0,
					  "y": 0
					},
					"id": 4,
					"legend": {
					  "avg": false,
					  "current": false,
					  "max": false,
					  "min": false,
					  "show": false,
					  "total": false,
					  "values": false
					},
					"lines": true,
					"linewidth": 1,
					"links": [],
					"nullPointMode": "null",
					"options": {},
					"percentage": false,
					"pointradius": 1,
					"points": true,
					"renderer": "flot",
					"seriesOverrides": [],
					"spaceLength": 10,
					"stack": false,
					"steppedLine": false,
					"targets": [
					  {
						"expr": "rate(galasa_runs_automated_started_total[1m])*60",
						"format": "time_series",
						"intervalFactor": 1,
						"refId": "A"
					  }
					],
					"thresholds": [],
					"timeFrom": null,
					"timeRegions": [],
					"timeShift": null,
					"title": "Automated Runs",
					"tooltip": {
					  "shared": true,
					  "sort": 0,
					  "value_type": "individual"
					},
					"type": "graph",
					"xaxis": {
					  "buckets": null,
					  "mode": "time",
					  "name": null,
					  "show": true,
					  "values": []
					},
					"yaxes": [
					  {
						"format": "short",
						"label": "per minute",
						"logBase": 1,
						"max": null,
						"min": "0",
						"show": true
					  },
					  {
						"format": "short",
						"label": null,
						"logBase": 1,
						"max": null,
						"min": null,
						"show": true
					  }
					],
					"yaxis": {
					  "align": false,
					  "alignLevel": null
					}
				  },
				  {
					"aliasColors": {},
					"bars": false,
					"dashLength": 10,
					"dashes": false,
					"fill": 1,
					"gridPos": {
					  "h": 10,
					  "w": 12,
					  "x": 12,
					  "y": 0
					},
					"id": 8,
					"legend": {
					  "avg": false,
					  "current": false,
					  "max": false,
					  "min": false,
					  "show": false,
					  "total": false,
					  "values": false
					},
					"lines": true,
					"linewidth": 1,
					"links": [],
					"nullPointMode": "null",
					"options": {},
					"percentage": false,
					"pointradius": 1,
					"points": true,
					"renderer": "flot",
					"seriesOverrides": [],
					"spaceLength": 10,
					"stack": false,
					"steppedLine": false,
					"targets": [
					  {
						"expr": "rate(galasa_runs_local_started_total[1m])*60",
						"format": "time_series",
						"intervalFactor": 1,
						"refId": "A"
					  }
					],
					"thresholds": [],
					"timeFrom": null,
					"timeRegions": [],
					"timeShift": null,
					"title": "Local Runs",
					"tooltip": {
					  "shared": true,
					  "sort": 0,
					  "value_type": "individual"
					},
					"type": "graph",
					"xaxis": {
					  "buckets": null,
					  "mode": "time",
					  "name": null,
					  "show": true,
					  "values": []
					},
					"yaxes": [
					  {
						"format": "short",
						"label": "per minute",
						"logBase": 1,
						"max": null,
						"min": "0",
						"show": true
					  },
					  {
						"format": "short",
						"label": null,
						"logBase": 1,
						"max": null,
						"min": null,
						"show": true
					  }
					],
					"yaxis": {
					  "align": false,
					  "alignLevel": null
					}
				  },
				  {
					"aliasColors": {
					  "{groups=\"test\",instance=\"galasa-ecosystem-metrics-external-service:9010\",job=\"metrics\"}": "dark-orange"
					},
					"bars": false,
					"dashLength": 10,
					"dashes": false,
					"fill": 1,
					"gridPos": {
					  "h": 11,
					  "w": 12,
					  "x": 0,
					  "y": 10
					},
					"id": 6,
					"legend": {
					  "avg": false,
					  "current": false,
					  "max": false,
					  "min": false,
					  "show": false,
					  "total": false,
					  "values": false
					},
					"lines": true,
					"linewidth": 1,
					"links": [],
					"nullPointMode": "null",
					"options": {},
					"percentage": false,
					"pointradius": 1,
					"points": true,
					"renderer": "flot",
					"seriesOverrides": [],
					"spaceLength": 10,
					"stack": false,
					"steppedLine": false,
					"targets": [
					  {
						"expr": "rate(galasa_runs_made_to_wait_total[1m])*60",
						"format": "time_series",
						"intervalFactor": 1,
						"refId": "A"
					  }
					],
					"thresholds": [],
					"timeFrom": null,
					"timeRegions": [],
					"timeShift": null,
					"title": "Run Waits",
					"tooltip": {
					  "shared": true,
					  "sort": 0,
					  "value_type": "individual"
					},
					"type": "graph",
					"xaxis": {
					  "buckets": null,
					  "mode": "time",
					  "name": null,
					  "show": true,
					  "values": []
					},
					"yaxes": [
					  {
						"format": "short",
						"label": "per minute",
						"logBase": 1,
						"max": null,
						"min": "0",
						"show": true
					  },
					  {
						"format": "short",
						"label": null,
						"logBase": 1,
						"max": null,
						"min": null,
						"show": true
					  }
					],
					"yaxis": {
					  "align": false,
					  "alignLevel": null
					}
				  },
				  {
					"aliasColors": {
					  "{groups=\"test\",instance=\"galasa-ecosystem-metrics-external-service:9010\",job=\"metrics\"}": "dark-orange"
					},
					"bars": false,
					"dashLength": 10,
					"dashes": false,
					"fill": 1,
					"gridPos": {
					  "h": 11,
					  "w": 12,
					  "x": 12,
					  "y": 10
					},
					"id": 2,
					"legend": {
					  "avg": false,
					  "current": false,
					  "max": false,
					  "min": false,
					  "show": false,
					  "total": false,
					  "values": false
					},
					"lines": true,
					"linewidth": 1,
					"links": [],
					"nullPointMode": "null",
					"options": {},
					"percentage": false,
					"pointradius": 1,
					"points": true,
					"renderer": "flot",
					"seriesOverrides": [],
					"spaceLength": 10,
					"stack": false,
					"steppedLine": false,
					"targets": [
					  {
						"expr": "rate(galasa_zos_insufficent_slots_total[1m])*60",
						"format": "time_series",
						"intervalFactor": 1,
						"refId": "A"
					  }
					],
					"thresholds": [],
					"timeFrom": null,
					"timeRegions": [],
					"timeShift": null,
					"title": "Insufficient zOS slots",
					"tooltip": {
					  "shared": true,
					  "sort": 0,
					  "value_type": "individual"
					},
					"type": "graph",
					"xaxis": {
					  "buckets": null,
					  "mode": "time",
					  "name": null,
					  "show": true,
					  "values": []
					},
					"yaxes": [
					  {
						"format": "short",
						"label": "per minute",
						"logBase": 1,
						"max": null,
						"min": "0",
						"show": true
					  },
					  {
						"format": "short",
						"label": null,
						"logBase": 1,
						"max": null,
						"min": null,
						"show": true
					  }
					],
					"yaxis": {
					  "align": false,
					  "alignLevel": null
					}
				  }
				],
				"refresh": "5s",
				"schemaVersion": 18,
				"style": "dark",
				"tags": [],
				"templating": {
				  "list": []
				},
				"time": {
				  "from": "now-15m",
				  "to": "now"
				},
				"timepicker": {
				  "refresh_intervals": [
					"5s",
					"10s",
					"30s",
					"1m",
					"5m",
					"15m",
					"30m",
					"1h",
					"2h",
					"1d"
				  ],
				  "time_options": [
					"5m",
					"15m",
					"1h",
					"6h",
					"12h",
					"24h",
					"2d",
					"7d",
					"30d"
				  ]
				},
				"timezone": "",
				"title": "Auto Generated Board",
				"uid": "WLQt0IMWz",
				"version": 1
			  }
`,
		},
	}
}

func generateDashboardConfigMap(cr *galasav1alpha1.GalasaEcosystem) *corev1.ConfigMap {
	return &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "grafana-dashboard",
			Namespace: cr.Namespace,
			Labels: map[string]string{
				"app": cr.Name + "-grafana",
			},
		},
		Data: map[string]string{
			"dashboards.yaml": `
apiVersion: 1
providers:
- name: 'Prometheus'
  orgId: 1
  folder: ''
  type: file
  disableDeletion: false
  editable: true
  options:
    path: /etc/grafana/json
`,
		},
	}
}

func generateProvisioningConfigMap(cr *galasav1alpha1.GalasaEcosystem) *corev1.ConfigMap {
	return &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "grafana-provisioning",
			Namespace: cr.Namespace,
			Labels: map[string]string{
				"app": cr.Name + "-grafana",
			},
		},
		Data: map[string]string{
			"prometheus.yaml": `
apiVersion: 1
datasources:
  - name: prometheus
    type: prometheus
    url: http://` + cr.Name + `-prometheus-internal-service:9090  
    access: proxy
    basicAuth: false
    isDefault: true
    jsonData:
      tlsAuth: false
      tlsAuthWithCACert: false
      tlsSkipVerify: true
    secureJsonData:
      tlsCACert: "..."
      tlsClientCert: "..."
      tlsClientKey: "..."
    version: 1
    editable: true
`,
		},
	}
}

func generateGrafanaConfig(cr *galasav1alpha1.GalasaEcosystem) *corev1.ConfigMap {
	var domain string
	if cr.Spec.IngressHostname != "" {
		domain = strings.Replace(cr.Spec.IngressHostname, "https://", "", 1)
	} else {
		domain = strings.Replace(cr.Spec.ExternalHostname, "http://", "", 1)
	}

	return &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "grafana-config",
			Namespace: cr.Namespace,
			Labels: map[string]string{
				"app": cr.Name + "-grafana",
			},
		},
		Data: map[string]string{
			"grafana.ini": `
##################### Grafana Configuration Example #####################
#
# Everything has defaults so you only need to uncomment things you want to
# change

# possible values : production, development
;app_mode = production

# instance name, defaults to HOSTNAME environment variable value or hostname if HOSTNAME var is empty
;instance_name = ${HOSTNAME}

#################################### Paths ####################################
[paths]
# Path to where grafana can store temp files, sessions, and the sqlite3 db (if that is used)
;data = /var/lib/grafana

# Temporary files in "data" directory older than given duration will be removed
;temp_data_lifetime = 24h

# Directory where grafana can store logs
;logs = /var/log/grafana

# Directory where grafana will automatically scan and look for plugins
;plugins = /var/lib/grafana/plugins

# folder that contains provisioning config files that grafana will apply on startup and while running.
;provisioning = conf/provisioning

#################################### Server ####################################
[server]
# Protocol (http, https, socket)
;protocol = http

# The ip address to bind to, empty will bind to all interfaces
;http_addr =

# The http port  to use
;http_port = 3000

# The public facing domain name used to access grafana from a browser
domain = ` + domain + `

# Redirect to correct domain if host header does not match domain
# Prevents DNS rebinding attacks
;enforce_domain = false

# The full public facing url you use in browser, used for redirects and emails
# If you use reverse proxy and sub path specify full url (with sub path)
root_url = %(protocol)s://%(domain)s:%(http_port)s/galasa-grafana/
serve_from_sub_path = true

# Log web requests
;router_logging = false

# the path relative working path
;static_root_path = public

# enable gzip
;enable_gzip = false

# https certs & key file
;cert_file =
;cert_key =

# Unix socket path
;socket =

#################################### Database ####################################
[database]
# You can configure the database connection by specifying type, host, name, user and password
# as separate properties or as on string using the url properties.

# Either "mysql", "postgres" or "sqlite3", it's your choice
;type = sqlite3
;host = 127.0.0.1:3306
;name = grafana
;user = root
# If the password contains # or ; you have to wrap it with triple quotes. Ex """#password;"""
;password =

# Use either URL or the previous fields to configure the database
# Example: mysql://user:secret@host:port/database
;url =

# For "postgres" only, either "disable", "require" or "verify-full"
;ssl_mode = disable

# For "sqlite3" only, path relative to data_path setting
;path = grafana.db

# Max idle conn setting default is 2
;max_idle_conn = 2

# Max conn setting default is 0 (mean not set)
;max_open_conn =

# Connection Max Lifetime default is 14400 (means 14400 seconds or 4 hours)
;conn_max_lifetime = 14400

# Set to true to log the sql calls and execution times.
log_queries =

# For "sqlite3" only. cache mode setting used for connecting to the database. (private, shared)
;cache_mode = private

#################################### Cache server #############################
[remote_cache]
# Either "redis", "memcached" or "database" default is "database"
;type = database

# cache connectionstring options
# database: will use Grafana primary database.
# redis: config like redis server e.g. "addr=127.0.0.1:6379,pool_size=100,db=grafana"
# memcache: 127.0.0.1:11211
;connstr =

#################################### Data proxy ###########################
[dataproxy]

# This enables data proxy logging, default is false
;logging = false

# How long the data proxy should wait before timing out default is 30 (seconds)
;timeout = 30

# If enabled and user is not anonymous, data proxy will add X-Grafana-User header with username into the request, default is false.
;send_user_header = false

#################################### Analytics ####################################
[analytics]
# Server reporting, sends usage counters to stats.grafana.org every 24 hours.
# No ip addresses are being tracked, only simple counters to track
# running instances, dashboard and error counts. It is very helpful to us.
# Change this option to false to disable reporting.
;reporting_enabled = true

# Set to false to disable all checks to https://grafana.net
# for new vesions (grafana itself and plugins), check is used
# in some UI views to notify that grafana or plugin update exists
# This option does not cause any auto updates, nor send any information
# only a GET request to http://grafana.com to get latest versions
;check_for_updates = true

# Google Analytics universal tracking code, only enabled if you specify an id here
;google_analytics_ua_id =

# Google Tag Manager ID, only enabled if you specify an id here
;google_tag_manager_id =

#################################### Security ####################################
[security]
# default admin user, created on startup
;admin_user = admin

# default admin password, can be changed before first start of grafana,  or in profile settings
;admin_password = admin

# used for signing
;secret_key = SW2YcwTIb9zpOOhoPsMm

# disable gravatar profile images
;disable_gravatar = false

# data source proxy whitelist (ip_or_domain:port separated by spaces)
;data_source_proxy_whitelist =

# disable protection against brute force login attempts
;disable_brute_force_login_protection = false

# set to true if you host Grafana behind HTTPS. default is false.
;cookie_secure = false

# set cookie SameSite attribute. defaults to "lax". can be set to "lax", "strict" and "none"
;cookie_samesite = lax

# set to true if you want to allow browsers to render Grafana in a <frame>, <iframe>, <embed> or <object>. default is false.
;allow_embedding = false

#################################### Snapshots ###########################
[snapshots]
# snapshot sharing options
;external_enabled = true
;external_snapshot_url = https://snapshots-origin.raintank.io
;external_snapshot_name = Publish to snapshot.raintank.io

# remove expired snapshot
;snapshot_remove_expired = true

#################################### Dashboards History ##################
[dashboards]
# Number dashboard versions to keep (per dashboard). Default: 20, Minimum: 1
;versions_to_keep = 20

#################################### Users ###############################
[users]
# disable user signup / registration
allow_sign_up = true

# Allow non admin users to create organizations
allow_org_create = false

# Set to true to automatically assign new users to the default organization (id 1)
auto_assign_org = true

# Default role new users will be automatically assigned (if disabled above is set to true)
auto_assign_org_role = Editor

# Background text for the user field on the login page
;login_hint = email or username
;password_hint = password

# Default UI theme ("dark" or "light")
;default_theme = dark

# External user management, these options affect the organization users view
;external_manage_link_url =
;external_manage_link_name =
;external_manage_info =

# Viewers can edit/inspect dashboard settings in the browser. But not save the dashboard.
viewers_can_edit = true

# Editors can administrate dashboard, folders and teams they create
editors_can_admin = true

[auth]
# Login cookie name
;login_cookie_name = grafana_session

# The lifetime (days) an authenticated user can be inactive before being required to login at next visit. Default is 7 days,
;login_maximum_inactive_lifetime_days = 7

# The maximum lifetime (days) an authenticated user can be logged in since login time before being required to login. Default is 30 days.
;login_maximum_lifetime_days = 30

# How often should auth tokens be rotated for authenticated users when being active. The default is each 10 minutes.
;token_rotation_interval_minutes = 10

# Set to true to disable (hide) the login form, useful if you use OAuth, defaults to false
disable_login_form = true

# Set to true to disable the signout link in the side menu. useful if you use auth.proxy, defaults to false
;disable_signout_menu = false

# URL to redirect the user to after sign out
;signout_redirect_url =

# Set to true to attempt login with OAuth automatically, skipping the login screen.
# This setting is ignored if multiple OAuth providers are configured.
;oauth_auto_login = true

#################################### Anonymous Auth ######################
[auth.anonymous]
# enable anonymous access
enabled = true

# specify organization name that should be used for unauthenticated users
;org_name = ORGANIZATION

# specify role for unauthenticated users
;org_role = editor

#################################### Github Auth ##########################
[auth.github]
;enabled = false
;allow_sign_up = true
;client_id = some_id
;client_secret = some_secret
;scopes = user:email,read:org
;auth_url = https://github.com/login/oauth/authorize
;token_url = https://github.com/login/oauth/access_token
;api_url = https://api.github.com/user
;team_ids =
;allowed_organizations =

#################################### Google Auth ##########################
[auth.google]
;enabled = false
;allow_sign_up = true
;client_id = some_client_id
;client_secret = some_client_secret
;scopes = https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email
;auth_url = https://accounts.google.com/o/oauth2/auth
;token_url = https://accounts.google.com/o/oauth2/token
;api_url = https://www.googleapis.com/oauth2/v1/userinfo
;allowed_domains =

#################################### Generic OAuth ##########################
[auth.generic_oauth]
;enabled = false
;name = OAuth
;allow_sign_up = true
;client_id = some_id
;client_secret = some_secret
;scopes = user:email,read:org
;auth_url = https://foo.bar/login/oauth/authorize
;token_url = https://foo.bar/login/oauth/access_token
;api_url = https://foo.bar/user
;team_ids =
;allowed_organizations =
;tls_skip_verify_insecure = false
;tls_client_cert =
;tls_client_key =
;tls_client_ca =

; Set to true to enable sending client_id and client_secret via POST body instead of Basic authentication HTTP header
; This might be required if the OAuth provider is not RFC6749 compliant, only supporting credentials passed via POST payload
;send_client_credentials_via_post = false

#################################### Grafana.com Auth ####################
[auth.grafana_com]
;enabled = false
;allow_sign_up = true
;client_id = some_id
;client_secret = some_secret
;scopes = user:email
;allowed_organizations =

#################################### Auth Proxy ##########################
[auth.proxy]
;enabled = false
;header_name = X-WEBAUTH-USER
;header_property = username
;auto_sign_up = true
;ldap_sync_ttl = 60
;whitelist = 192.168.1.1, 192.168.2.1
;headers = Email:X-User-Email, Name:X-User-Name

#################################### Basic Auth ##########################
[auth.basic]
enabled = false

#################################### Auth LDAP ##########################
[auth.ldap]
;enabled = false
;config_file = /etc/grafana/ldap.toml
;allow_sign_up = true

#################################### SMTP / Emailing ##########################
[smtp]
;enabled = false
;host = localhost:25
;user =
# If the password contains # or ; you have to wrap it with trippel quotes. Ex """#password;"""
;password =
;cert_file =
;key_file =
;skip_verify = false
;from_address = admin@grafana.localhost
;from_name = Grafana
# EHLO identity in SMTP dialog (defaults to instance_name)
;ehlo_identity = dashboard.example.com

[emails]
;welcome_email_on_sign_up = false

#################################### Logging ##########################
[log]
# Either "console", "file", "syslog". Default is console and  file
# Use space to separate multiple modes, e.g. "console file"
;mode = console file

# Either "debug", "info", "warn", "error", "critical", default is "info"
;level = info

# optional settings to set different levels for specific loggers. Ex filters = sqlstore:debug
;filters =

# For "console" mode only
[log.console]
;level =

# log line format, valid options are text, console and json
;format = console

# For "file" mode only
[log.file]
;level =

# log line format, valid options are text, console and json
;format = text

# This enables automated log rotate(switch of following options), default is true
;log_rotate = true

# Max line number of single file, default is 1000000
;max_lines = 1000000

# Max size shift of single file, default is 28 means 1 << 28, 256MB
;max_size_shift = 28

# Segment log daily, default is true
;daily_rotate = true

# Expired days of log file(delete after max days), default is 7
;max_days = 7

[log.syslog]
;level =

# log line format, valid options are text, console and json
;format = text

# Syslog network type and address. This can be udp, tcp, or unix. If left blank, the default unix endpoints will be used.
;network =
;address =

# Syslog facility. user, daemon and local0 through local7 are valid.
;facility =

# Syslog tag. By default, the process' argv[0] is used.
;tag =

#################################### Alerting ############################
[alerting]
# Disable alerting engine & UI features
;enabled = true
# Makes it possible to turn off alert rule execution but alerting UI is visible
;execute_alerts = true

# Default setting for new alert rules. Defaults to categorize error and timeouts as alerting. (alerting, keep_state)
;error_or_timeout = alerting

# Default setting for how Grafana handles nodata or null values in alerting. (alerting, no_data, keep_state, ok)
;nodata_or_nullvalues = no_data

# Alert notifications can include images, but rendering many images at the same time can overload the server
# This limit will protect the server from render overloading and make sure notifications are sent out quickly
;concurrent_render_limit = 5


# Default setting for alert calculation timeout. Default value is 30
;evaluation_timeout_seconds = 30

# Default setting for alert notification timeout. Default value is 30
;notification_timeout_seconds = 30

# Default setting for max attempts to sending alert notifications. Default value is 3
;max_attempts = 3

#################################### Explore #############################
[explore]
# Enable the Explore section
;enabled = true

#################################### Internal Grafana Metrics ##########################
# Metrics available at HTTP API Url /metrics
[metrics]
# Disable / Enable internal metrics
;enabled           = true

# Publish interval
;interval_seconds  = 10

# Send internal metrics to Graphite
[metrics.graphite]
# Enable by setting the address setting (ex localhost:2003)
;address =
;prefix = prod.grafana.%(instance_name)s.

#################################### Distributed tracing ############
[tracing.jaeger]
# Enable by setting the address sending traces to jaeger (ex localhost:6831)
;address = localhost:6831
# Tag that will always be included in when creating new spans. ex (tag1:value1,tag2:value2)
;always_included_tag = tag1:value1
# Type specifies the type of the sampler: const, probabilistic, rateLimiting, or remote
;sampler_type = const
# jaeger samplerconfig param
# for "const" sampler, 0 or 1 for always false/true respectively
# for "probabilistic" sampler, a probability between 0 and 1
# for "rateLimiting" sampler, the number of spans per second
# for "remote" sampler, param is the same as for "probabilistic"
# and indicates the initial sampling rate before the actual one
# is received from the mothership
;sampler_param = 1

#################################### Grafana.com integration  ##########################
# Url used to import dashboards directly from Grafana.com
[grafana_com]
;url = https://grafana.com

#################################### External image storage ##########################
[external_image_storage]
# Used for uploading images to public servers so they can be included in slack/email messages.
# you can choose between (s3, webdav, gcs, azure_blob, local)
;provider =

[external_image_storage.s3]
;bucket =
;region =
;path =
;access_key =
;secret_key =

[external_image_storage.webdav]
;url =
;public_url =
;username =
;password =

[external_image_storage.gcs]
;key_file =
;bucket =
;path =

[external_image_storage.azure_blob]
;account_name =
;account_key =
;container_name =

[external_image_storage.local]
# does not require any configuration

[rendering]
# Options to configure external image rendering server like https://github.com/grafana/grafana-image-renderer
;server_url =
;callback_url =

[enterprise]
# Path to a valid Grafana Enterprise license.jwt file
;license_path =

[panels]
# If set to true Grafana will allow script tags in text panels. Not recommended as it enable XSS vulnerabilities.
;disable_sanitize_html = false

[plugins]
;enable_alpha = false
;app_tls_skip_verify_insecure = false
`,
		},
	}
}

func generateGrafanaPVC(cr *galasav1alpha1.GalasaEcosystem) *corev1.PersistentVolumeClaim {
	return &corev1.PersistentVolumeClaim{
		ObjectMeta: metav1.ObjectMeta{
			Name:      cr.Name + "-grafana-pvc",
			Namespace: cr.Namespace,
			Labels: map[string]string{
				"app": cr.Name + "-grafana",
			},
		},
		Spec: corev1.PersistentVolumeClaimSpec{
			AccessModes: []corev1.PersistentVolumeAccessMode{
				corev1.ReadWriteOnce,
			},
			StorageClassName: cr.Spec.StorageClassName,
			Resources: corev1.ResourceRequirements{
				Requests: corev1.ResourceList{
					corev1.ResourceName(corev1.ResourceStorage): resource.MustParse("200m"),
				},
			},
		},
	}
}

func generateGrafanaDeployment(cr *galasav1alpha1.GalasaEcosystem) *appsv1.Deployment {
	defaultMode := int32(420)
	return &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      cr.Name + "-grafana",
			Namespace: cr.Namespace,
			Labels: map[string]string{
				"app": cr.Name + "-grafana",
			},
		},
		Spec: appsv1.DeploymentSpec{
			Replicas: cr.Spec.Monitoring.GrafanaReplicas,
			Selector: &metav1.LabelSelector{
				MatchLabels: map[string]string{
					"app": cr.Name + "-grafana",
				},
			},
			Template: corev1.PodTemplateSpec{
				ObjectMeta: metav1.ObjectMeta{
					Name: cr.Name + "-grafana",
					Labels: map[string]string{
						"app": cr.Name + "-grafana",
					},
				},
				Spec: corev1.PodSpec{
					NodeSelector: cr.Spec.Monitoring.NodeSelector,
					InitContainers: []corev1.Container{
						{
							Name:            "init-chown-data",
							Image:           "busybox:latest",
							ImagePullPolicy: corev1.PullIfNotPresent,
							Command: []string{
								"chown",
								"472:472",
								"/var/lib/grafana",
							},
							VolumeMounts: []corev1.VolumeMount{
								{
									Name:      "data",
									MountPath: "/var/lib/grafana",
									SubPath:   "",
								},
							},
						},
					},
					Containers: []corev1.Container{
						{
							Name:            "grafana",
							Image:           "grafana/grafana",
							ImagePullPolicy: corev1.PullIfNotPresent,
							Ports: []corev1.ContainerPort{
								{
									Name:          "grafana",
									ContainerPort: 3000,
								},
							},
							VolumeMounts: []corev1.VolumeMount{
								{
									Name:      "data",
									MountPath: "/var/lib/grafana",
								},
								{
									Name:      "grafana-config",
									MountPath: "/etc/grafana/",
								},
								{
									Name:      "grafana-provisioning",
									MountPath: "/etc/grafana/provisioning/datasources",
								},
								{
									Name:      "grafana-dashboard",
									MountPath: "/etc/grafana/provisioning/dashboards",
								},
								{
									Name:      "grafana-auto-dashboard",
									MountPath: "/etc/grafana/json",
								},
							},
						},
					},
					Volumes: []corev1.Volume{
						{
							Name: "grafana-config",
							VolumeSource: corev1.VolumeSource{
								ConfigMap: &corev1.ConfigMapVolumeSource{
									LocalObjectReference: corev1.LocalObjectReference{
										Name: "grafana-config",
									},
									DefaultMode: &defaultMode,
								},
							},
						},
						{
							Name: "grafana-provisioning",
							VolumeSource: corev1.VolumeSource{
								ConfigMap: &corev1.ConfigMapVolumeSource{
									LocalObjectReference: corev1.LocalObjectReference{
										Name: "grafana-provisioning",
									},
									DefaultMode: &defaultMode,
								},
							},
						},
						{
							Name: "grafana-dashboard",
							VolumeSource: corev1.VolumeSource{
								ConfigMap: &corev1.ConfigMapVolumeSource{
									LocalObjectReference: corev1.LocalObjectReference{
										Name: "grafana-dashboard",
									},
									DefaultMode: &defaultMode,
								},
							},
						},
						{
							Name: "grafana-auto-dashboard",
							VolumeSource: corev1.VolumeSource{
								ConfigMap: &corev1.ConfigMapVolumeSource{
									LocalObjectReference: corev1.LocalObjectReference{
										Name: "grafana-auto-dashboard",
									},
									DefaultMode: &defaultMode,
								},
							},
						},
						{
							Name: "data",
							VolumeSource: corev1.VolumeSource{
								PersistentVolumeClaim: &corev1.PersistentVolumeClaimVolumeSource{
									ClaimName: cr.Name + "-grafana-pvc",
								},
							},
						},
					},
				},
			},
		},
	}
}

func generateGrafanaExposedService(cr *galasav1alpha1.GalasaEcosystem) *corev1.Service {
	return &corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Name:      cr.Name + "-grafana-external-service",
			Namespace: cr.Namespace,
		},
		Spec: corev1.ServiceSpec{
			Type: corev1.ServiceType(corev1.ServiceTypeNodePort),
			Ports: []corev1.ServicePort{
				{
					Name:       "grafana",
					TargetPort: intstr.FromInt(3000),
					Port:       3000,
				},
			},
			Selector: map[string]string{
				"app": cr.Name + "-grafana",
			},
		},
	}
}

func generateGrafanaInternalService(cr *galasav1alpha1.GalasaEcosystem) *corev1.Service {
	return &corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Name:      cr.Name + "-grafana-internal-service",
			Namespace: cr.Namespace,
			Labels: map[string]string{
				"app": cr.Name + "-grafana",
			},
		},
		Spec: corev1.ServiceSpec{
			Ports: []corev1.ServicePort{
				{
					Name:       "metrics",
					TargetPort: intstr.FromInt(9090),
					Port:       9090,
				},
			},
			Selector: map[string]string{
				"app": cr.Name + "-grafana",
			},
		},
	}
}
