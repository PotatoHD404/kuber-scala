// Generated Case Classes
case class Access(
                   dataLens: Option[Boolean],
                   dataTransfer: Option[Boolean]
                 )

case class Access(
                   dataLens: Option[Boolean],
                   dataTransfer: Option[Boolean],
                   metrika: Option[Boolean],
                   serverless: Option[Boolean],
                   webSql: Option[Boolean],
                   yandexQuery: Option[Boolean]
                 )

case class Access(
                   dataLens: Option[Boolean],
                   dataTransfer: Option[Boolean],
                   serverless: Option[Boolean],
                   webSql: Option[Boolean]
                 )

case class Access(
                   dataLens: Option[Boolean],
                   dataTransfer: Option[Boolean],
                   webSql: Option[Boolean]
                 )

case class Access(
                   dataTransfer: Option[Boolean]
                 )

case class Address(
                    externalIpv4Address: Option[List[ExternalIpv4Address]],
                    externalIpv6Address: Option[List[ExternalIpv6Address]],
                    internalIpv4Address: Option[List[InternalIpv4Address]]
                  )

case class AllocationPolicy(
                             location: Option[List[Location]]
                           )

case class AllocationPolicy(
                             location: Option[Set[Location]]
                           )

case class AllocationPolicy(
                             location: Set[Location]
                           )

case class AllocationPolicy(
                             zones: Option[Set[Option[String]]]
                           )

case class AllocationPolicy(
                             zones: Set[Option[String]]
                           )

case class AltNames(
                     fromName: Option[String],
                     toName: Option[String]
                   )

case class AndPrincipals(
                          any: Option[Boolean],
                          header: Option[List[Header]],
                          remoteIp: Option[String]
                        )

case class AndPrincipals(
                          any: Option[Boolean],
                          headers: Option[List[Headers]],
                          remoteIp: Option[String]
                        )

case class AnonymousAccessFlags(
                                 configRead: Option[Boolean],
                                 list: Option[Boolean],
                                 read: Option[Boolean]
                               )

case class ApplicationBalancerState(
                                     statusMessage: Option[String],
                                     targetGroupId: Option[String]
                                   )

case class ApplicationLoadBalancer(
                                    maxOpeningTrafficDuration: Option[Int],
                                    statusMessage: Option[String],
                                    targetGroupDescription: Option[String],
                                    targetGroupId: Option[String],
                                    targetGroupLabels: Option[Map[String, Option[String]]],
                                    targetGroupName: Option[String]
                                  )

case class ApplyServerSideEncryptionByDefault(
                                               kmsMasterKeyId: String,
                                               sseAlgorithm: String
                                             )

case class AttachedTargetGroup(
                                healthcheck: List[Healthcheck],
                                targetGroupId: String
                              )

case class AttachedTargetGroup(
                                healthcheck: Option[List[Healthcheck]],
                                targetGroupId: Option[String]
                              )

case class AuditLog(
                     filter: Option[String],
                     runtimeConfiguration: Option[Boolean]
                   )

case class AutoScale(
                      cpuUtilizationTarget: Option[Double],
                      customRule: Option[List[CustomRule]],
                      initialSize: Int,
                      maxSize: Option[Int],
                      measurementDuration: Int,
                      minZoneSize: Option[Int],
                      stabilizationDuration: Option[Int],
                      warmupDuration: Option[Int]
                    )

case class AutoScale(
                      cpuUtilizationTarget: Option[Double],
                      customRule: Option[List[CustomRule]],
                      initialSize: Option[Int],
                      maxSize: Option[Int],
                      measurementDuration: Option[Int],
                      minZoneSize: Option[Int],
                      stabilizationDuration: Option[Int],
                      warmupDuration: Option[Int]
                    )

case class AutoScale(
                      initial: Int,
                      max: Int,
                      min: Int
                    )

case class AutoScale(
                      initial: Option[Int],
                      max: Option[Int],
                      min: Option[Int]
                    )

case class Automatic(

                    )

case class AutoscalingConfig(
                              cpuUtilizationTarget: Option[Double],
                              decommissionTimeout: Option[Int],
                              maxHostsCount: Int,
                              measurementDuration: Option[Int],
                              preemptible: Option[Boolean],
                              stabilizationDuration: Option[Int],
                              warmupDuration: Option[Int]
                            )

case class AutoscalingConfig(
                              cpuUtilizationTarget: Option[Double],
                              decommissionTimeout: Option[Int],
                              maxHostsCount: Option[Int],
                              measurementDuration: Option[Int],
                              preemptible: Option[Boolean],
                              stabilizationDuration: Option[Int],
                              warmupDuration: Option[Int]
                            )

case class BackupWindowStart(
                              hours: Option[Int],
                              minutes: Option[Int]
                            )

case class Binding(
                    members: Set[Option[String]],
                    role: String
                  )

case class BootDisk(
                     autoDelete: Option[Boolean],
                     deviceName: Option[String],
                     diskId: Option[String],
                     initializeParams: Option[List[InitializeParams]],
                     mode: Option[String]
                   )

case class BootDisk(
                     deviceName: Option[String],
                     diskId: Option[String],
                     initializeParams: Option[List[InitializeParams]],
                     mode: Option[String]
                   )

case class BootDisk(
                     size: Option[Int],
                     `type`: Option[String]
                   )

case class Challenges(
                       createdAt: Option[String],
                       dnsName: Option[String],
                       dnsType: Option[String],
                       dnsValue: Option[String],
                       domain: Option[String],
                       httpContent: Option[String],
                       httpUrl: Option[String],
                       message: Option[String],
                       `type`: Option[String],
                       updatedAt: Option[String]
                     )

case class Chart(
                  chartId: Option[String],
                  description: Option[String],
                  displayLegend: Option[Boolean],
                  freeze: Option[String],
                  nameHidingSettings: Option[List[NameHidingSettings]],
                  queries: Option[List[Queries]],
                  seriesOverrides: Option[List[SeriesOverrides]],
                  title: Option[String],
                  visualizationSettings: Option[List[VisualizationSettings]]
                )

case class Cilium(

                 )

case class Cilium(
                   routingMode: Option[String]
                 )

case class Clickhouse(
                       config: Option[List[Config]],
                       resources: Option[List[Resources]]
                     )

case class ClickhouseSource(
                             connection: Option[List[Connection]],
                             excludeTables: Option[List[Option[String]]],
                             includeTables: Option[List[Option[String]]],
                             securityGroups: Option[List[Option[String]]],
                             subnetId: Option[String]
                           )

case class ClickhouseTarget(
                             altNames: Option[List[AltNames]],
                             cleanupPolicy: Option[String],
                             clickhouseClusterName: Option[String],
                             connection: Option[List[Connection]],
                             securityGroups: Option[List[Option[String]]],
                             sharding: Option[List[Sharding]],
                             subnetId: Option[String]
                           )

case class CloudStorage(
                         dataCacheEnabled: Option[Boolean],
                         dataCacheMaxSize: Option[Int],
                         enabled: Boolean,
                         moveFactor: Option[Double]
                       )

case class CloudStorage(
                         dataCacheEnabled: Option[Boolean],
                         dataCacheMaxSize: Option[Int],
                         enabled: Option[Boolean],
                         moveFactor: Option[Double]
                       )

case class ClusterConfig(
                          access: Option[List[Access]],
                          backupWindowStart: Option[List[BackupWindowStart]],
                          featureCompatibilityVersion: Option[String],
                          mongocfg: Option[List[Mongocfg]],
                          mongod: Option[List[Mongod]],
                          mongos: Option[List[Mongos]],
                          version: Option[String]
                        )

case class ClusterConfig(
                          access: Option[List[Access]],
                          backupWindowStart: Option[List[BackupWindowStart]],
                          featureCompatibilityVersion: Option[String],
                          mongocfg: Option[List[Mongocfg]],
                          mongod: Option[List[Mongod]],
                          mongos: Option[List[Mongos]],
                          version: String
                        )

case class ClusterConfig(
                          hadoop: Option[List[Hadoop]],
                          subclusterSpec: List[SubclusterSpec],
                          versionId: Option[String]
                        )

case class ClusterConfig(
                          hadoop: Option[List[Hadoop]],
                          subclusterSpec: Option[List[SubclusterSpec]],
                          versionId: Option[String]
                        )

case class Collections(
                        collectionName: Option[String],
                        databaseName: Option[String]
                      )

case class ColorSchemeSettings(
                                automatic: Option[List[Automatic]],
                                gradient: Option[List[Gradient]],
                                standard: Option[List[Standard]]
                              )

case class ColumnValueHash(
                            columnName: Option[String]
                          )

case class Command(
                    args: Option[List[Option[String]]],
                    env: Option[Map[String, Option[String]]],
                    path: String
                  )

case class Compression(
                        method: Option[String],
                        minPartSize: Option[Int],
                        minPartSizeRatio: Option[Double]
                      )

case class Compression(
                        method: String,
                        minPartSize: Int,
                        minPartSizeRatio: Double
                      )

case class Config(
                   access: Option[List[Access]],
                   assignPublicIp: Option[Boolean],
                   brokersCount: Option[Int],
                   kafka: List[Kafka],
                   schemaRegistry: Option[Boolean],
                   unmanagedTopics: Option[Boolean],
                   version: String,
                   zones: List[Option[String]],
                   zookeeper: Option[List[Zookeeper]]
                 )

case class Config(
                   access: Option[List[Access]],
                   autofailover: Option[Boolean],
                   backupRetainPeriodDays: Option[Int],
                   backupWindowStart: Option[List[BackupWindowStart]],
                   performanceDiagnostics: Option[List[PerformanceDiagnostics]],
                   poolerConfig: Option[List[PoolerConfig]],
                   postgresqlConfig: Option[Map[String, Option[String]]],
                   resources: List[Resources],
                   version: String
                 )

case class Config(
                   access: Option[List[Access]],
                   autofailover: Option[Boolean],
                   backupRetainPeriodDays: Option[Int],
                   backupWindowStart: Option[List[BackupWindowStart]],
                   performanceDiagnostics: Option[List[PerformanceDiagnostics]],
                   poolerConfig: Option[List[PoolerConfig]],
                   postgresqlConfig: Option[Map[String, Option[String]]],
                   resources: Option[List[Resources]],
                   version: Option[String]
                 )

case class Config(
                   adminPassword: Option[String],
                   dataNode: Option[List[DataNode]],
                   edition: Option[String],
                   masterNode: Option[List[MasterNode]],
                   plugins: Option[Set[Option[String]]],
                   version: Option[String]
                 )

case class Config(
                   adminPassword: String,
                   dataNode: List[DataNode],
                   edition: Option[String],
                   masterNode: Option[List[MasterNode]],
                   plugins: Option[Set[Option[String]]],
                   version: Option[String]
                 )

case class Config(
                   backgroundFetchesPoolSize: Option[Int],
                   backgroundPoolSize: Option[Int],
                   backgroundSchedulePoolSize: Option[Int],
                   compression: Option[List[Compression]],
                   defaultDatabase: Option[String],
                   geobaseUri: Option[String],
                   graphiteRollup: Option[List[GraphiteRollup]],
                   kafka: Option[List[Kafka]],
                   kafkaTopic: Option[List[KafkaTopic]],
                   keepAliveTimeout: Option[Int],
                   logLevel: Option[String],
                   markCacheSize: Option[Int],
                   maxConcurrentQueries: Option[Int],
                   maxConnections: Option[Int],
                   maxPartitionSizeToDrop: Option[Int],
                   maxTableSizeToDrop: Option[Int],
                   mergeTree: Option[List[MergeTree]],
                   metricLogEnabled: Option[Boolean],
                   metricLogRetentionSize: Option[Int],
                   metricLogRetentionTime: Option[Int],
                   partLogRetentionSize: Option[Int],
                   partLogRetentionTime: Option[Int],
                   queryLogRetentionSize: Option[Int],
                   queryLogRetentionTime: Option[Int],
                   queryThreadLogEnabled: Option[Boolean],
                   queryThreadLogRetentionSize: Option[Int],
                   queryThreadLogRetentionTime: Option[Int],
                   rabbitmq: Option[List[Rabbitmq]],
                   textLogEnabled: Option[Boolean],
                   textLogLevel: Option[String],
                   textLogRetentionSize: Option[Int],
                   textLogRetentionTime: Option[Int],
                   timezone: Option[String],
                   totalMemoryProfilerStep: Option[Int],
                   traceLogEnabled: Option[Boolean],
                   traceLogRetentionSize: Option[Int],
                   traceLogRetentionTime: Option[Int],
                   uncompressedCacheSize: Option[Int]
                 )

case class Config(
                   clientOutputBufferLimitNormal: Option[String],
                   clientOutputBufferLimitPubsub: Option[String],
                   databases: Option[Int],
                   maxmemoryPolicy: Option[String],
                   notifyKeyspaceEvents: Option[String],
                   password: String,
                   slowlogLogSlowerThan: Option[Int],
                   slowlogMaxLen: Option[Int],
                   timeout: Option[Int],
                   version: String
                 )

case class Config(
                   clientOutputBufferLimitNormal: Option[String],
                   clientOutputBufferLimitPubsub: Option[String],
                   databases: Option[Int],
                   maxmemoryPolicy: Option[String],
                   notifyKeyspaceEvents: Option[String],
                   slowlogLogSlowerThan: Option[Int],
                   slowlogMaxLen: Option[Int],
                   timeout: Option[Int],
                   version: Option[String]
                 )

case class Connection(
                       connectionOptions: Option[List[ConnectionOptions]]
                     )

case class Connection(
                       mdbClusterId: Option[String],
                       onPremise: Option[List[OnPremise]]
                     )

case class Connection(
                       sourceIp: Option[Boolean]
                     )

case class ConnectionLimits(
                             maxConnectionsPerHour: Option[Int],
                             maxQuestionsPerHour: Option[Int],
                             maxUpdatesPerHour: Option[Int],
                             maxUserConnections: Option[Int]
                           )

case class ConnectionOptions(
                              authSource: Option[String],
                              mdbClusterId: Option[String],
                              onPremise: Option[List[OnPremise]],
                              password: Option[List[Password]],
                              user: Option[String]
                            )

case class ConnectionOptions(
                              database: Option[String],
                              mdbClusterId: Option[String],
                              onPremise: Option[List[OnPremise]],
                              password: Option[List[Password]],
                              user: Option[String]
                            )

case class Connectivity(
                         networkId: String
                       )

case class ConnectorConfigMirrormaker(
                                       replicationFactor: Int,
                                       sourceCluster: List[SourceCluster],
                                       targetCluster: List[TargetCluster],
                                       topics: String
                                     )

case class ConnectorConfigMirrormaker(
                                       replicationFactor: Option[Int],
                                       sourceCluster: Option[List[SourceCluster]],
                                       targetCluster: Option[List[TargetCluster]],
                                       topics: Option[String]
                                     )

case class ConnectorConfigS3Sink(
                                  fileCompressionType: Option[String],
                                  fileMaxRecords: Option[Int],
                                  s3Connection: Option[List[S3Connection]],
                                  topics: Option[String]
                                )

case class ConnectorConfigS3Sink(
                                  fileCompressionType: String,
                                  fileMaxRecords: Option[Int],
                                  s3Connection: List[S3Connection],
                                  topics: String
                                )

case class Consumer(
                     name: String,
                     serviceType: Option[String],
                     startingMessageTimestampMs: Option[Int],
                     supportedCodecs: Option[List[Option[String]]]
                   )

case class Container(
                      id: Option[String],
                      path: Option[String],
                      retryAttempts: Option[String],
                      retryInterval: Option[String],
                      serviceAccountId: Option[String]
                    )

case class Container(
                      id: String,
                      path: Option[String],
                      retryAttempts: Option[String],
                      retryInterval: Option[String],
                      serviceAccountId: Option[String]
                    )

case class ContainerRuntime(
                             `type`: String
                           )

case class Content(
                    zipFilename: String
                  )

case class Cookie(
                   name: Option[String],
                   ttl: Option[String]
                 )

case class Cookie(
                   name: String,
                   ttl: Option[String]
                 )

case class CorsRule(
                     allowedHeaders: Option[List[Option[String]]],
                     allowedMethods: List[Option[String]],
                     allowedOrigins: List[Option[String]],
                     exposeHeaders: Option[List[Option[String]]],
                     maxAgeSeconds: Option[Int]
                   )

case class CurrentVersion(
                           createdAt: Option[String],
                           description: Option[String],
                           destroyAt: Option[String],
                           id: Option[String],
                           payloadEntryKeys: Option[List[Option[String]]],
                           secretId: Option[String],
                           status: Option[String]
                         )

case class Custom(
                   defaultValues: Option[List[Option[String]]],
                   multiselectable: Option[Boolean],
                   values: Option[List[Option[String]]]
                 )

case class CustomDomains(
                          certificateId: String,
                          domainId: Option[String],
                          fqdn: String
                        )

case class CustomRule(
                       folderId: Option[String],
                       labels: Option[Map[String, Option[String]]],
                       metricName: Option[String],
                       metricType: Option[String],
                       ruleType: Option[String],
                       service: Option[String],
                       target: Option[Double]
                     )

case class CustomRule(
                       folderId: Option[String],
                       labels: Option[Map[String, Option[String]]],
                       metricName: String,
                       metricType: String,
                       ruleType: String,
                       service: Option[String],
                       target: Double
                     )

case class DataNode(
                     resources: List[Resources]
                   )

case class DataNode(
                     resources: Option[List[Resources]]
                   )

case class Database(
                     extension: Option[Set[Extension]],
                     lcCollate: Option[String],
                     lcType: Option[String],
                     name: String,
                     owner: String,
                     templateDb: Option[String]
                   )

case class Database(
                     name: Option[String]
                   )

case class Database(
                     name: String
                   )

case class DefaultHandler(
                           certificateIds: Option[Set[Option[String]]],
                           httpHandler: Option[List[HttpHandler]],
                           streamHandler: Option[List[StreamHandler]]
                         )

case class DefaultHandler(
                           certificateIds: Set[Option[String]],
                           httpHandler: Option[List[HttpHandler]],
                           streamHandler: Option[List[StreamHandler]]
                         )

case class DefaultRetention(
                             days: Option[Int],
                             mode: String,
                             years: Option[Int]
                           )

case class DeployPolicy(
                         maxCreating: Option[Int],
                         maxDeleting: Option[Int],
                         maxExpansion: Int,
                         maxUnavailable: Int,
                         startupDuration: Option[Int],
                         strategy: Option[String]
                       )

case class DeployPolicy(
                         maxCreating: Option[Int],
                         maxDeleting: Option[Int],
                         maxExpansion: Option[Int],
                         maxUnavailable: Option[Int],
                         startupDuration: Option[Int],
                         strategy: Option[String]
                       )

case class DeployPolicy(
                         maxExpansion: Int,
                         maxUnavailable: Int
                       )

case class DeployPolicy(
                         maxExpansion: Option[Int],
                         maxUnavailable: Option[Int]
                       )

case class DhcpOptions(
                        domainName: Option[String],
                        domainNameServers: Option[List[Option[String]]],
                        ntpServers: Option[List[Option[String]]]
                      )

case class DirectResponseAction(
                                 body: Option[String],
                                 status: Option[Int]
                               )

case class Disabled(

                   )

case class DiscardRule(
                        discardPercent: Option[Int],
                        grpcCodes: Option[List[Option[String]]],
                        httpCodeIntervals: Option[List[Option[String]]],
                        httpCodes: Option[List[Option[Int]]]
                      )

case class DiskPlacementPolicy(
                                diskPlacementGroupId: String
                              )

case class Dlq(
                queueId: Option[String],
                serviceAccountId: Option[String]
              )

case class Dlq(
                queueId: String,
                serviceAccountId: String
              )

case class DnsRecord(
                      dnsZoneId: Option[String],
                      fqdn: Option[String],
                      ptr: Option[Boolean],
                      ttl: Option[Int]
                    )

case class DnsRecord(
                      dnsZoneId: Option[String],
                      fqdn: String,
                      ptr: Option[Boolean],
                      ttl: Option[Int]
                    )

case class Downsampling(
                         disabled: Option[Boolean],
                         gapFilling: Option[String],
                         gridAggregation: Option[String],
                         gridInterval: Option[Int],
                         maxPoints: Option[Int]
                       )

case class Egress(
                   description: Option[String],
                   fromPort: Option[Int],
                   id: Option[String],
                   labels: Option[Map[String, Option[String]]],
                   port: Option[Int],
                   predefinedTarget: Option[String],
                   protocol: Option[String],
                   securityGroupId: Option[String],
                   toPort: Option[Int],
                   v4CidrBlocks: Option[List[Option[String]]],
                   v6CidrBlocks: Option[List[Option[String]]]
                 )

case class Egress(
                   description: Option[String],
                   fromPort: Option[Int],
                   id: Option[String],
                   labels: Option[Map[String, Option[String]]],
                   port: Option[Int],
                   predefinedTarget: Option[String],
                   protocol: String,
                   securityGroupId: Option[String],
                   toPort: Option[Int],
                   v4CidrBlocks: Option[List[Option[String]]],
                   v6CidrBlocks: Option[List[Option[String]]]
                 )

case class Enabled(
                    caCertificate: Option[String]
                  )

case class Endpoint(
                     address: List[Address],
                     ports: List[Option[Int]]
                   )

case class Endpoint(
                     address: Option[List[Address]],
                     ports: Option[List[Option[Int]]]
                   )

case class Entries(
                    command: Option[List[Command]],
                    key: String,
                    textValue: Option[String]
                  )

case class Entries(
                    key: Option[String],
                    textValue: Option[String]
                  )

case class ExcludedCollections(
                                collectionName: Option[String],
                                databaseName: Option[String]
                              )

case class Expiration(
                       date: Option[String],
                       days: Option[Int],
                       expiredObjectDeleteMarker: Option[Boolean]
                     )

case class Extension(
                      name: String,
                      version: Option[String]
                    )

case class ExternalAddressSpec(
                                address: Option[String],
                                ipVersion: Option[String]
                              )

case class ExternalCluster(
                            bootstrapServers: Option[String],
                            saslMechanism: Option[String],
                            saslPassword: Option[String],
                            saslUsername: Option[String],
                            securityProtocol: Option[String]
                          )

case class ExternalCluster(
                            bootstrapServers: String,
                            saslMechanism: Option[String],
                            saslPassword: Option[String],
                            saslUsername: Option[String],
                            securityProtocol: Option[String]
                          )

case class ExternalIpv4Address(
                                address: Option[String]
                              )

case class ExternalIpv4Address(
                                address: Option[String],
                                ddosProtectionProvider: Option[String],
                                outgoingSmtpCapability: Option[String],
                                zoneId: Option[String]
                              )

case class ExternalIpv6Address(
                                address: Option[String]
                              )

case class ExternalS3(
                       accessKeyId: Option[String],
                       endpoint: Option[String],
                       region: Option[String],
                       secretAccessKey: Option[String]
                     )

case class ExternalS3(
                       accessKeyId: Option[String],
                       endpoint: String,
                       region: Option[String],
                       secretAccessKey: Option[String]
                     )

case class Filesystem(
                       deviceName: Option[String],
                       filesystemId: Option[String],
                       mode: Option[String]
                     )

case class Filesystem(
                       deviceName: Option[String],
                       filesystemId: String,
                       mode: Option[String]
                     )

case class FixedScale(
                       size: Int
                     )

case class FixedScale(
                       size: Option[Int]
                     )

case class FormatSchema(
                         name: Option[String],
                         `type`: Option[String],
                         uri: Option[String]
                       )

case class FormatSchema(
                         name: String,
                         `type`: String,
                         uri: String
                       )

case class Fqmn(
                 exact: Option[String],
                 prefix: Option[String],
                 regex: Option[String]
               )

case class Function(
                     id: Option[String],
                     retryAttempts: Option[String],
                     retryInterval: Option[String],
                     serviceAccountId: Option[String],
                     tag: Option[String]
                   )

case class Function(
                     id: String,
                     retryAttempts: Option[String],
                     retryInterval: Option[String],
                     serviceAccountId: Option[String],
                     tag: Option[String]
                   )

case class Gradient(
                     greenValue: Option[String],
                     redValue: Option[String],
                     violetValue: Option[String],
                     yellowValue: Option[String]
                   )

case class Grant(
                  id: Option[String],
                  permissions: Set[Option[String]],
                  `type`: String,
                  uri: Option[String]
                )

case class GraphiteRollup(
                           name: Option[String],
                           pattern: Option[List[Pattern]]
                         )

case class GraphiteRollup(
                           name: String,
                           pattern: Option[List[Pattern]]
                         )

case class GrpcBackend(
                        healthcheck: Option[List[Healthcheck]],
                        loadBalancingConfig: Option[List[LoadBalancingConfig]],
                        name: Option[String],
                        port: Option[Int],
                        targetGroupIds: Option[List[Option[String]]],
                        tls: Option[List[Tls]],
                        weight: Option[Int]
                      )

case class GrpcBackend(
                        healthcheck: Option[List[Healthcheck]],
                        loadBalancingConfig: Option[List[LoadBalancingConfig]],
                        name: String,
                        port: Option[Int],
                        targetGroupIds: List[Option[String]],
                        tls: Option[List[Tls]],
                        weight: Option[Int]
                      )

case class GrpcHealthcheck(
                            serviceName: Option[String]
                          )

case class GrpcMatch(
                      fqmn: Option[List[Fqmn]]
                    )

case class GrpcRoute(
                      grpcMatch: Option[List[GrpcMatch]],
                      grpcRouteAction: Option[List[GrpcRouteAction]],
                      grpcStatusResponseAction: Option[List[GrpcStatusResponseAction]]
                    )

case class GrpcRouteAction(
                            autoHostRewrite: Option[Boolean],
                            backendGroupId: Option[String],
                            hostRewrite: Option[String],
                            idleTimeout: Option[String],
                            maxTimeout: Option[String]
                          )

case class GrpcRouteAction(
                            autoHostRewrite: Option[Boolean],
                            backendGroupId: String,
                            hostRewrite: Option[String],
                            idleTimeout: Option[String],
                            maxTimeout: Option[String]
                          )

case class GrpcStatusResponseAction(
                                     status: Option[String]
                                   )

case class Hadoop(
                   properties: Option[Map[String, Option[String]]],
                   services: Option[Set[Option[String]]],
                   sshPublicKeys: Option[Set[Option[String]]]
                 )

case class Handler(
                    allowHttp10: Option[Boolean],
                    http2Options: Option[List[Http2Options]],
                    httpRouterId: Option[String],
                    rewriteRequestId: Option[Boolean]
                  )

case class Handler(
                    backendGroupId: Option[String]
                  )

case class Handler(
                    certificateIds: Option[Set[Option[String]]],
                    httpHandler: Option[List[HttpHandler]],
                    streamHandler: Option[List[StreamHandler]]
                  )

case class Handler(
                    certificateIds: Set[Option[String]],
                    httpHandler: Option[List[HttpHandler]],
                    streamHandler: Option[List[StreamHandler]]
                  )

case class Header(
                   headerName: Option[String]
                 )

case class Header(
                   headerName: String
                 )

case class Header(
                   name: String,
                   value: Option[List[Value]]
                 )

case class Headers(
                    name: Option[String],
                    value: Option[List[Value]]
                  )

case class HealthCheck(
                        healthyThreshold: Option[Int],
                        httpOptions: Option[List[HttpOptions]],
                        interval: Option[Int],
                        tcpOptions: Option[List[TcpOptions]],
                        timeout: Option[Int],
                        unhealthyThreshold: Option[Int]
                      )

case class Healthcheck(
                        grpcHealthcheck: Option[List[GrpcHealthcheck]],
                        healthcheckPort: Option[Int],
                        healthyThreshold: Option[Int],
                        httpHealthcheck: Option[List[HttpHealthcheck]],
                        interval: Option[String],
                        intervalJitterPercent: Option[Double],
                        streamHealthcheck: Option[List[StreamHealthcheck]],
                        timeout: Option[String],
                        unhealthyThreshold: Option[Int]
                      )

case class Healthcheck(
                        grpcHealthcheck: Option[List[GrpcHealthcheck]],
                        healthcheckPort: Option[Int],
                        healthyThreshold: Option[Int],
                        httpHealthcheck: Option[List[HttpHealthcheck]],
                        interval: String,
                        intervalJitterPercent: Option[Double],
                        streamHealthcheck: Option[List[StreamHealthcheck]],
                        timeout: String,
                        unhealthyThreshold: Option[Int]
                      )

case class Healthcheck(
                        healthyThreshold: Option[Int],
                        httpOptions: Option[List[HttpOptions]],
                        interval: Option[Int],
                        name: Option[String],
                        tcpOptions: Option[List[TcpOptions]],
                        timeout: Option[Int],
                        unhealthyThreshold: Option[Int]
                      )

case class Healthcheck(
                        healthyThreshold: Option[Int],
                        httpOptions: Option[List[HttpOptions]],
                        interval: Option[Int],
                        name: String,
                        tcpOptions: Option[List[TcpOptions]],
                        timeout: Option[Int],
                        unhealthyThreshold: Option[Int]
                      )

case class HeatmapSettings(
                            greenValue: Option[String],
                            redValue: Option[String],
                            violetValue: Option[String],
                            yellowValue: Option[String]
                          )

case class Host(
                 assignPublicIp: Option[Boolean],
                 backupPriority: Option[Int],
                 fqdn: Option[String],
                 name: Option[String],
                 priority: Option[Int],
                 replicationSource: Option[String],
                 replicationSourceName: Option[String],
                 subnetId: Option[String],
                 zone: String
               )

case class Host(
                 assignPublicIp: Option[Boolean],
                 backupPriority: Option[Int],
                 fqdn: Option[String],
                 priority: Option[Int],
                 replicationSource: Option[String],
                 subnetId: Option[String],
                 zone: Option[String]
               )

case class Host(
                 assignPublicIp: Option[Boolean],
                 fqdn: Option[String],
                 name: Option[String],
                 priority: Option[Int],
                 replicationSource: Option[String],
                 replicationSourceName: Option[String],
                 role: Option[String],
                 subnetId: Option[String],
                 zone: String
               )

case class Host(
                 assignPublicIp: Option[Boolean],
                 fqdn: Option[String],
                 name: Option[String],
                 subnetId: Option[String],
                 `type`: Option[String],
                 zone: Option[String]
               )

case class Host(
                 assignPublicIp: Option[Boolean],
                 fqdn: Option[String],
                 name: String,
                 subnetId: Option[String],
                 `type`: String,
                 zone: String
               )

case class Host(
                 assignPublicIp: Option[Boolean],
                 fqdn: Option[String],
                 priority: Option[Int],
                 replicationSource: Option[String],
                 role: Option[String],
                 subnetId: Option[String],
                 zone: Option[String]
               )

case class Host(
                 assignPublicIp: Option[Boolean],
                 fqdn: Option[String],
                 replicaPriority: Option[Int],
                 shardName: Option[String],
                 subnetId: Option[String],
                 zone: Option[String]
               )

case class Host(
                 assignPublicIp: Option[Boolean],
                 fqdn: Option[String],
                 replicaPriority: Option[Int],
                 shardName: Option[String],
                 subnetId: Option[String],
                 zone: String
               )

case class Host(
                 assignPublicIp: Option[Boolean],
                 fqdn: Option[String],
                 shardName: Option[String],
                 subnetId: Option[String],
                 `type`: Option[String],
                 zone: Option[String]
               )

case class Host(
                 assignPublicIp: Option[Boolean],
                 fqdn: Option[String],
                 shardName: Option[String],
                 subnetId: Option[String],
                 `type`: String,
                 zone: String
               )

case class Host(
                 assignPublicIp: Option[Boolean],
                 fqdn: Option[String],
                 subnetId: Option[String],
                 zone: Option[String]
               )

case class Host(
                 assignPublicIp: Option[Boolean],
                 fqdn: Option[String],
                 subnetId: Option[String],
                 zone: String
               )

case class Host(
                 assignPublicIp: Option[Boolean],
                 health: Option[String],
                 name: Option[String],
                 role: Option[String],
                 shardName: Option[String],
                 subnetId: Option[String],
                 `type`: Option[String],
                 zoneId: Option[String]
               )

case class Host(
                 assignPublicIp: Option[Boolean],
                 health: Option[String],
                 name: Option[String],
                 role: Option[String],
                 shardName: Option[String],
                 subnetId: String,
                 `type`: Option[String],
                 zoneId: String
               )

case class Host(
                 assignPublicIp: Option[Boolean],
                 health: Option[String],
                 name: Option[String],
                 role: Option[String],
                 subnetId: Option[String],
                 zoneId: Option[String]
               )

case class HostAffinityRules(
                              key: String,
                              op: String,
                              values: List[Option[String]]
                            )

case class Http(
                 handler: Option[List[Handler]],
                 redirects: Option[List[Redirects]]
               )

case class Http2Options(
                         maxConcurrentStreams: Option[Int]
                       )

case class HttpBackend(
                        healthcheck: Option[List[Healthcheck]],
                        http2: Option[Boolean],
                        loadBalancingConfig: Option[List[LoadBalancingConfig]],
                        name: Option[String],
                        port: Option[Int],
                        storageBucket: Option[String],
                        targetGroupIds: Option[List[Option[String]]],
                        tls: Option[List[Tls]],
                        weight: Option[Int]
                      )

case class HttpBackend(
                        healthcheck: Option[List[Healthcheck]],
                        http2: Option[Boolean],
                        loadBalancingConfig: Option[List[LoadBalancingConfig]],
                        name: String,
                        port: Option[Int],
                        storageBucket: Option[String],
                        targetGroupIds: Option[List[Option[String]]],
                        tls: Option[List[Tls]],
                        weight: Option[Int]
                      )

case class HttpHandler(
                        allowHttp10: Option[Boolean],
                        http2Options: Option[List[Http2Options]],
                        httpRouterId: Option[String],
                        rewriteRequestId: Option[Boolean]
                      )

case class HttpHealthcheck(
                            host: Option[String],
                            http2: Option[Boolean],
                            path: Option[String]
                          )

case class HttpHealthcheck(
                            host: Option[String],
                            http2: Option[Boolean],
                            path: String
                          )

case class HttpMatch(
                      httpMethod: Option[Set[Option[String]]],
                      path: Option[List[Path]]
                    )

case class HttpOptions(
                        path: Option[String],
                        port: Int
                      )

case class HttpOptions(
                        path: Option[String],
                        port: Option[Int]
                      )

case class HttpOptions(
                        path: String,
                        port: Int
                      )

case class HttpRoute(
                      directResponseAction: Option[List[DirectResponseAction]],
                      httpMatch: Option[List[HttpMatch]],
                      httpRouteAction: Option[List[HttpRouteAction]],
                      redirectAction: Option[List[RedirectAction]]
                    )

case class HttpRouteAction(
                            autoHostRewrite: Option[Boolean],
                            backendGroupId: Option[String],
                            hostRewrite: Option[String],
                            idleTimeout: Option[String],
                            prefixRewrite: Option[String],
                            timeout: Option[String],
                            upgradeTypes: Option[Set[Option[String]]]
                          )

case class HttpRouteAction(
                            autoHostRewrite: Option[Boolean],
                            backendGroupId: String,
                            hostRewrite: Option[String],
                            idleTimeout: Option[String],
                            prefixRewrite: Option[String],
                            timeout: Option[String],
                            upgradeTypes: Option[Set[Option[String]]]
                          )

case class Https(
                  certificateId: String
                )

case class Image(
                  args: Option[List[Option[String]]],
                  command: Option[List[Option[String]]],
                  digest: Option[String],
                  environment: Option[Map[String, Option[String]]],
                  url: Option[String],
                  workDir: Option[String]
                )

case class Image(
                  args: Option[List[Option[String]]],
                  command: Option[List[Option[String]]],
                  digest: Option[String],
                  environment: Option[Map[String, Option[String]]],
                  url: String,
                  workDir: Option[String]
                )

case class Ingress(
                    description: Option[String],
                    fromPort: Option[Int],
                    id: Option[String],
                    labels: Option[Map[String, Option[String]]],
                    port: Option[Int],
                    predefinedTarget: Option[String],
                    protocol: Option[String],
                    securityGroupId: Option[String],
                    toPort: Option[Int],
                    v4CidrBlocks: Option[List[Option[String]]],
                    v6CidrBlocks: Option[List[Option[String]]]
                  )

case class Ingress(
                    description: Option[String],
                    fromPort: Option[Int],
                    id: Option[String],
                    labels: Option[Map[String, Option[String]]],
                    port: Option[Int],
                    predefinedTarget: Option[String],
                    protocol: String,
                    securityGroupId: Option[String],
                    toPort: Option[Int],
                    v4CidrBlocks: Option[List[Option[String]]],
                    v6CidrBlocks: Option[List[Option[String]]]
                  )

case class InitializeParams(
                             blockSize: Option[Int],
                             description: Option[String],
                             imageId: Option[String],
                             name: Option[String],
                             size: Option[Int],
                             snapshotId: Option[String],
                             `type`: Option[String]
                           )

case class InitializeParams(
                             description: Option[String],
                             imageId: Option[String],
                             size: Option[Int],
                             snapshotId: Option[String],
                             `type`: Option[String]
                           )

case class InstanceTemplate(
                             bootDisk: List[BootDisk],
                             description: Option[String],
                             hostname: Option[String],
                             labels: Option[Map[String, Option[String]]],
                             metadata: Option[Map[String, Option[String]]],
                             name: Option[String],
                             networkInterface: List[NetworkInterface],
                             networkSettings: Option[List[NetworkSettings]],
                             placementPolicy: Option[List[PlacementPolicy]],
                             platformId: Option[String],
                             resources: List[Resources],
                             schedulingPolicy: Option[List[SchedulingPolicy]],
                             secondaryDisk: Option[List[SecondaryDisk]],
                             serviceAccountId: Option[String]
                           )

case class InstanceTemplate(
                             bootDisk: Option[List[BootDisk]],
                             containerRuntime: Option[List[ContainerRuntime]],
                             labels: Option[Map[String, Option[String]]],
                             metadata: Option[Map[String, Option[String]]],
                             name: Option[String],
                             nat: Option[Boolean],
                             networkAccelerationType: Option[String],
                             networkInterface: Option[List[NetworkInterface]],
                             placementPolicy: Option[List[PlacementPolicy]],
                             platformId: Option[String],
                             resources: Option[List[Resources]],
                             schedulingPolicy: Option[List[SchedulingPolicy]]
                           )

case class InstanceTemplate(
                             bootDisk: Option[List[BootDisk]],
                             description: Option[String],
                             hostname: Option[String],
                             labels: Option[Map[String, Option[String]]],
                             metadata: Option[Map[String, Option[String]]],
                             name: Option[String],
                             networkInterface: Option[List[NetworkInterface]],
                             networkSettings: Option[List[NetworkSettings]],
                             placementPolicy: Option[List[PlacementPolicy]],
                             platformId: Option[String],
                             resources: Option[List[Resources]],
                             schedulingPolicy: Option[List[SchedulingPolicy]],
                             secondaryDisk: Option[List[SecondaryDisk]],
                             serviceAccountId: Option[String]
                           )

case class Instances(
                      fqdn: Option[String],
                      instanceId: Option[String],
                      name: Option[String],
                      networkInterface: Option[List[NetworkInterface]],
                      status: Option[String],
                      statusChangedAt: Option[String],
                      statusMessage: Option[String],
                      zoneId: Option[String]
                    )

case class InternalAddressSpec(
                                address: Option[String],
                                ipVersion: Option[String],
                                subnetId: Option[String]
                              )

case class InternalAddressSpec(
                                address: Option[String],
                                ipVersion: Option[String],
                                subnetId: String
                              )

case class InternalIpv4Address(
                                address: Option[String],
                                subnetId: Option[String]
                              )

case class Iot(
                deviceId: Option[String],
                registryId: Option[String],
                topic: Option[String]
              )

case class Iot(
                deviceId: Option[String],
                registryId: String,
                topic: Option[String]
              )

case class Ipv4DnsRecords(
                           dnsZoneId: Option[String],
                           fqdn: Option[String],
                           ptr: Option[Boolean],
                           ttl: Option[Int]
                         )

case class Ipv4DnsRecords(
                           dnsZoneId: Option[String],
                           fqdn: String,
                           ptr: Option[Boolean],
                           ttl: Option[Int]
                         )

case class Ipv6DnsRecord(
                          dnsZoneId: Option[String],
                          fqdn: Option[String],
                          ptr: Option[Boolean],
                          ttl: Option[Int]
                        )

case class Ipv6DnsRecord(
                          dnsZoneId: Option[String],
                          fqdn: String,
                          ptr: Option[Boolean],
                          ttl: Option[Int]
                        )

case class Ipv6DnsRecords(
                           dnsZoneId: Option[String],
                           fqdn: Option[String],
                           ptr: Option[Boolean],
                           ttl: Option[Int]
                         )

case class Ipv6DnsRecords(
                           dnsZoneId: Option[String],
                           fqdn: String,
                           ptr: Option[Boolean],
                           ttl: Option[Int]
                         )

case class Journal(
                    commitInterval: Option[Int]
                  )

case class Kafka(
                  kafkaConfig: Option[List[KafkaConfig]],
                  resources: List[Resources]
                )

case class Kafka(
                  saslMechanism: Option[String],
                  saslPassword: Option[String],
                  saslUsername: Option[String],
                  securityProtocol: Option[String]
                )

case class KafkaConfig(
                        autoCreateTopicsEnable: Option[Boolean],
                        compressionType: Option[String],
                        defaultReplicationFactor: Option[String],
                        logFlushIntervalMessages: Option[String],
                        logFlushIntervalMs: Option[String],
                        logFlushSchedulerIntervalMs: Option[String],
                        logPreallocate: Option[Boolean],
                        logRetentionBytes: Option[String],
                        logRetentionHours: Option[String],
                        logRetentionMinutes: Option[String],
                        logRetentionMs: Option[String],
                        logSegmentBytes: Option[String],
                        messageMaxBytes: Option[String],
                        numPartitions: Option[String],
                        offsetsRetentionMinutes: Option[String],
                        replicaFetchMaxBytes: Option[String],
                        saslEnabledMechanisms: Option[Set[Option[String]]],
                        socketReceiveBufferBytes: Option[String],
                        socketSendBufferBytes: Option[String],
                        sslCipherSuites: Option[Set[Option[String]]]
                      )

case class KafkaTopic(
                       name: Option[String],
                       settings: Option[List[Settings]]
                     )

case class KafkaTopic(
                       name: String,
                       settings: Option[List[Settings]]
                     )

case class Kmip(
                 clientCertificate: Option[String],
                 keyIdentifier: Option[String],
                 port: Option[Int],
                 serverCa: Option[String],
                 serverName: Option[String]
               )

case class KmsProvider(
                        keyId: Option[String]
                      )

case class LabelValues(
                        defaultValues: Option[List[Option[String]]],
                        folderId: Option[String],
                        labelKey: Option[String],
                        multiselectable: Option[Boolean],
                        selectors: Option[String]
                      )

case class LabelValues(
                        defaultValues: Option[List[Option[String]]],
                        folderId: Option[String],
                        labelKey: String,
                        multiselectable: Option[Boolean],
                        selectors: Option[String]
                      )

case class Left(
                 max: Option[String],
                 min: Option[String],
                 precision: Option[Int],
                 title: Option[String],
                 `type`: Option[String],
                 unitFormat: Option[String]
               )

case class LifecycleRule(
                          abortIncompleteMultipartUploadDays: Option[Int],
                          enabled: Boolean,
                          expiration: Option[List[Expiration]],
                          id: Option[String],
                          noncurrentVersionExpiration: Option[List[NoncurrentVersionExpiration]],
                          noncurrentVersionTransition: Option[Set[NoncurrentVersionTransition]],
                          prefix: Option[String],
                          transition: Option[Set[Transition]]
                        )

case class Listener(
                     endpoint: Option[List[Endpoint]],
                     http: Option[List[Http]],
                     name: Option[String],
                     stream: Option[List[Stream]],
                     tls: Option[List[Tls]]
                   )

case class Listener(
                     endpoint: Option[List[Endpoint]],
                     http: Option[List[Http]],
                     name: String,
                     stream: Option[List[Stream]],
                     tls: Option[List[Tls]]
                   )

case class Listener(
                     externalAddressSpec: Option[Set[ExternalAddressSpec]],
                     internalAddressSpec: Option[Set[InternalAddressSpec]],
                     name: Option[String],
                     port: Option[Int],
                     protocol: Option[String],
                     targetPort: Option[Int]
                   )

case class Listener(
                     externalAddressSpec: Option[Set[ExternalAddressSpec]],
                     internalAddressSpec: Option[Set[InternalAddressSpec]],
                     name: String,
                     port: Int,
                     protocol: Option[String],
                     targetPort: Option[Int]
                   )

case class LoadBalancer(
                         maxOpeningTrafficDuration: Option[Int],
                         statusMessage: Option[String],
                         targetGroupDescription: Option[String],
                         targetGroupId: Option[String],
                         targetGroupLabels: Option[Map[String, Option[String]]],
                         targetGroupName: Option[String]
                       )

case class LoadBalancerState(
                              statusMessage: Option[String],
                              targetGroupId: Option[String]
                            )

case class LoadBalancingConfig(
                                localityAwareRoutingPercent: Option[Int],
                                mode: Option[String],
                                panicThreshold: Option[Int],
                                strictLocality: Option[Boolean]
                              )

case class LocalDisk(
                      deviceName: Option[String],
                      sizeBytes: Int
                    )

case class Location(
                     disableTraffic: Option[Boolean],
                     subnetId: Option[String],
                     zoneId: Option[String]
                   )

case class Location(
                     disableTraffic: Option[Boolean],
                     subnetId: String,
                     zoneId: String
                   )

case class Location(
                     region: Option[List[Region]]
                   )

case class Location(
                     region: Option[List[Region]],
                     zone: Option[List[Zone]]
                   )

case class Location(
                     subnetId: Option[String],
                     zone: Option[String]
                   )

case class LogGroup(
                     batchCutoff: Option[String],
                     batchSize: Option[String],
                     logGroupIds: Option[Set[Option[String]]]
                   )

case class LogGroup(
                     batchCutoff: String,
                     batchSize: Option[String],
                     logGroupIds: Set[Option[String]]
                   )

case class LogOptions(
                       disable: Option[Boolean],
                       discardRule: Option[List[DiscardRule]],
                       logGroupId: Option[String]
                     )

case class Logging(
                    batchCutoff: Option[String],
                    batchSize: Option[String],
                    groupId: Option[String],
                    levels: Option[Set[Option[String]]],
                    resourceIds: Option[Set[Option[String]]],
                    resourceTypes: Option[Set[Option[String]]]
                  )

case class Logging(
                    batchCutoff: String,
                    batchSize: Option[String],
                    groupId: String,
                    levels: Set[Option[String]],
                    resourceIds: Set[Option[String]],
                    resourceTypes: Set[Option[String]]
                  )

case class Logging(
                    targetBucket: String,
                    targetPrefix: Option[String]
                  )

case class MaintenancePolicy(
                              autoRepair: Boolean,
                              autoUpgrade: Boolean,
                              maintenanceWindow: Option[Set[MaintenanceWindow]]
                            )

case class MaintenancePolicy(
                              autoRepair: Option[Boolean],
                              autoUpgrade: Option[Boolean],
                              maintenanceWindow: Option[Set[MaintenanceWindow]]
                            )

case class MaintenancePolicy(
                              autoUpgrade: Boolean,
                              maintenanceWindow: Option[Set[MaintenanceWindow]]
                            )

case class MaintenancePolicy(
                              autoUpgrade: Option[Boolean],
                              maintenanceWindow: Option[Set[MaintenanceWindow]]
                            )

case class MaintenanceWindow(
                              day: Option[String],
                              duration: Option[String],
                              startTime: Option[String]
                            )

case class MaintenanceWindow(
                              day: Option[String],
                              duration: String,
                              startTime: String
                            )

case class MaintenanceWindow(
                              day: Option[String],
                              hour: Option[Int],
                              `type`: Option[String]
                            )

case class MaintenanceWindow(
                              day: Option[String],
                              hour: Option[Int],
                              `type`: String
                            )

case class Managed(
                    challengeCount: Option[Int],
                    challengeType: String
                  )

case class Master(
                   clusterCaCertificate: Option[String],
                   externalV4Address: Option[String],
                   externalV4Endpoint: Option[String],
                   externalV6Address: Option[String],
                   externalV6Endpoint: Option[String],
                   internalV4Address: Option[String],
                   internalV4Endpoint: Option[String],
                   maintenancePolicy: Option[List[MaintenancePolicy]],
                   masterLogging: Option[List[MasterLogging]],
                   publicIp: Option[Boolean],
                   regional: Option[List[Regional]],
                   securityGroupIds: Option[Set[Option[String]]],
                   version: Option[String],
                   versionInfo: Option[List[VersionInfo]],
                   zonal: Option[List[Zonal]]
                 )

case class MasterHosts(
                        assignPublicIp: Option[Boolean],
                        fqdn: Option[String]
                      )

case class MasterLogging(
                          clusterAutoscalerEnabled: Option[Boolean],
                          enabled: Option[Boolean],
                          eventsEnabled: Option[Boolean],
                          folderId: Option[String],
                          kubeApiserverEnabled: Option[Boolean],
                          logGroupId: Option[String]
                        )

case class MasterNode(
                       resources: List[Resources]
                     )

case class MasterNode(
                       resources: Option[List[Resources]]
                     )

case class MasterSubcluster(
                             resources: List[Resources]
                           )

case class MasterSubcluster(
                             resources: Option[List[Resources]]
                           )

case class Members(
                    id: Option[String],
                    `type`: Option[String]
                  )

case class MergeTree(
                      maxBytesToMergeAtMinSpaceInPool: Option[Int],
                      maxReplicatedMergesInQueue: Option[Int],
                      minBytesForWidePart: Option[Int],
                      minRowsForWidePart: Option[Int],
                      numberOfFreeEntriesInPoolToLowerMaxSizeOfMerge: Option[Int],
                      partsToDelayInsert: Option[Int],
                      partsToThrowInsert: Option[Int],
                      replicatedDeduplicationWindow: Option[Int],
                      replicatedDeduplicationWindowSeconds: Option[Int],
                      ttlOnlyDropParts: Option[Boolean]
                    )

case class MessageQueue(
                         batchCutoff: Option[String],
                         batchSize: Option[String],
                         queueId: Option[String],
                         serviceAccountId: Option[String],
                         visibilityTimeout: Option[String]
                       )

case class MessageQueue(
                         batchCutoff: String,
                         batchSize: Option[String],
                         queueId: String,
                         serviceAccountId: String,
                         visibilityTimeout: Option[String]
                       )

case class MetadataOptions(
                            awsV1HttpEndpoint: Option[Int],
                            awsV1HttpToken: Option[Int],
                            gceHttpEndpoint: Option[Int],
                            gceHttpToken: Option[Int]
                          )

case class MlModel(
                    name: Option[String],
                    `type`: Option[String],
                    uri: Option[String]
                  )

case class MlModel(
                    name: String,
                    `type`: String,
                    uri: String
                  )

case class ModifyRequestHeaders(
                                 append: Option[String],
                                 name: Option[String],
                                 remove: Option[Boolean],
                                 replace: Option[String]
                               )

case class ModifyRequestHeaders(
                                 append: Option[String],
                                 name: String,
                                 remove: Option[Boolean],
                                 replace: Option[String]
                               )

case class ModifyResponseHeaders(
                                  append: Option[String],
                                  name: Option[String],
                                  remove: Option[Boolean],
                                  replace: Option[String]
                                )

case class ModifyResponseHeaders(
                                  append: Option[String],
                                  name: String,
                                  remove: Option[Boolean],
                                  replace: Option[String]
                                )

case class MongoSource(
                        collections: Option[List[Collections]],
                        connection: Option[List[Connection]],
                        excludedCollections: Option[List[ExcludedCollections]],
                        secondaryPreferredMode: Option[Boolean],
                        securityGroups: Option[List[Option[String]]],
                        subnetId: Option[String]
                      )

case class MongoTarget(
                        cleanupPolicy: Option[String],
                        connection: Option[List[Connection]],
                        database: Option[String],
                        securityGroups: Option[List[Option[String]]],
                        subnetId: Option[String]
                      )

case class Mongocfg(
                     net: Option[List[Net]],
                     operationProfiling: Option[List[OperationProfiling]],
                     storage: Option[List[Storage]]
                   )

case class Mongod(
                   auditLog: Option[List[AuditLog]],
                   net: Option[List[Net]],
                   operationProfiling: Option[List[OperationProfiling]],
                   security: Option[List[Security]],
                   setParameter: Option[List[SetParameter]],
                   storage: Option[List[Storage]]
                 )

case class Mongos(
                   net: Option[List[Net]]
                 )

case class MysqlSource(
                        connection: Option[List[Connection]],
                        database: Option[String],
                        excludeTablesRegex: Option[List[Option[String]]],
                        includeTablesRegex: Option[List[Option[String]]],
                        objectTransferSettings: Option[List[ObjectTransferSettings]],
                        password: Option[List[Password]],
                        securityGroups: Option[List[Option[String]]],
                        serviceDatabase: Option[String],
                        timezone: Option[String],
                        user: Option[String]
                      )

case class MysqlTarget(
                        connection: Option[List[Connection]],
                        database: Option[String],
                        password: Option[List[Password]],
                        securityGroups: Option[List[Option[String]]],
                        skipConstraintChecks: Option[Boolean],
                        sqlMode: Option[String],
                        timezone: Option[String],
                        user: Option[String]
                      )

case class NameHidingSettings(
                               names: Option[List[Option[String]]],
                               positive: Option[Boolean]
                             )

case class NatDnsRecord(
                         dnsZoneId: Option[String],
                         fqdn: Option[String],
                         ptr: Option[Boolean],
                         ttl: Option[Int]
                       )

case class NatDnsRecord(
                         dnsZoneId: Option[String],
                         fqdn: String,
                         ptr: Option[Boolean],
                         ttl: Option[Int]
                       )

case class Net(
                maxIncomingConnections: Option[Int]
              )

case class NetworkImplementation(
                                  cilium: Option[List[Cilium]]
                                )

case class NetworkInterface(
                             dnsRecord: Option[List[DnsRecord]],
                             index: Option[Int],
                             ipAddress: Option[String],
                             ipv4: Option[Boolean],
                             ipv6: Option[Boolean],
                             ipv6Address: Option[String],
                             ipv6DnsRecord: Option[List[Ipv6DnsRecord]],
                             macAddress: Option[String],
                             nat: Option[Boolean],
                             natDnsRecord: Option[List[NatDnsRecord]],
                             natIpAddress: Option[String],
                             natIpVersion: Option[String],
                             securityGroupIds: Option[Set[Option[String]]],
                             subnetId: Option[String]
                           )

case class NetworkInterface(
                             dnsRecord: Option[List[DnsRecord]],
                             index: Option[Int],
                             ipAddress: Option[String],
                             ipv4: Option[Boolean],
                             ipv6: Option[Boolean],
                             ipv6Address: Option[String],
                             ipv6DnsRecord: Option[List[Ipv6DnsRecord]],
                             macAddress: Option[String],
                             nat: Option[Boolean],
                             natDnsRecord: Option[List[NatDnsRecord]],
                             natIpAddress: Option[String],
                             natIpVersion: Option[String],
                             securityGroupIds: Option[Set[Option[String]]],
                             subnetId: String
                           )

case class NetworkInterface(
                             dnsRecord: Option[List[DnsRecord]],
                             ipAddress: Option[String],
                             ipv4: Option[Boolean],
                             ipv6: Option[Boolean],
                             ipv6Address: Option[String],
                             ipv6DnsRecord: Option[List[Ipv6DnsRecord]],
                             nat: Option[Boolean],
                             natDnsRecord: Option[List[NatDnsRecord]],
                             natIpAddress: Option[String],
                             networkId: Option[String],
                             securityGroupIds: Option[Set[Option[String]]],
                             subnetIds: Option[Set[Option[String]]]
                           )

case class NetworkInterface(
                             index: Option[Int],
                             ipAddress: Option[String],
                             ipv4: Option[Boolean],
                             ipv6: Option[Boolean],
                             ipv6Address: Option[String],
                             macAddress: Option[String],
                             nat: Option[Boolean],
                             natIpAddress: Option[String],
                             natIpVersion: Option[String],
                             subnetId: Option[String]
                           )

case class NetworkInterface(
                             ipv4: Option[Boolean],
                             ipv4DnsRecords: Option[List[Ipv4DnsRecords]],
                             ipv6: Option[Boolean],
                             ipv6DnsRecords: Option[List[Ipv6DnsRecords]],
                             nat: Option[Boolean],
                             securityGroupIds: Option[Set[Option[String]]],
                             subnetIds: Option[Set[Option[String]]]
                           )

case class NetworkInterface(
                             ipv4: Option[Boolean],
                             ipv4DnsRecords: Option[List[Ipv4DnsRecords]],
                             ipv6: Option[Boolean],
                             ipv6DnsRecords: Option[List[Ipv6DnsRecords]],
                             nat: Option[Boolean],
                             securityGroupIds: Option[Set[Option[String]]],
                             subnetIds: Set[Option[String]]
                           )

case class NetworkSettings(
                            `type`: Option[String]
                          )

case class NoncurrentVersionExpiration(
                                        days: Option[Int]
                                      )

case class NoncurrentVersionTransition(
                                        days: Option[Int],
                                        storageClass: String
                                      )

case class ObjectLockConfiguration(
                                    objectLockEnabled: Option[String],
                                    rule: Option[List[Rule]]
                                  )

case class ObjectStorage(
                          bucketId: Option[String],
                          create: Option[Boolean],
                          delete: Option[Boolean],
                          prefix: Option[String],
                          suffix: Option[String],
                          update: Option[Boolean]
                        )

case class ObjectStorage(
                          bucketId: String,
                          create: Option[Boolean],
                          delete: Option[Boolean],
                          prefix: Option[String],
                          suffix: Option[String],
                          update: Option[Boolean]
                        )

case class ObjectTransferSettings(
                                   cast: Option[String],
                                   collation: Option[String],
                                   constraint: Option[String],
                                   defaultValues: Option[String],
                                   fkConstraint: Option[String],
                                   function: Option[String],
                                   index: Option[String],
                                   materializedView: Option[String],
                                   policy: Option[String],
                                   primaryKey: Option[String],
                                   rule: Option[String],
                                   sequence: Option[String],
                                   sequenceOwnedBy: Option[String],
                                   table: Option[String],
                                   trigger: Option[String],
                                   `type`: Option[String],
                                   view: Option[String]
                                 )

case class ObjectTransferSettings(
                                   routine: Option[String],
                                   trigger: Option[String],
                                   view: Option[String]
                                 )

case class OnPremise(
                      hosts: Option[List[Option[String]]],
                      port: Option[Int],
                      replicaSet: Option[String],
                      tlsMode: Option[List[TlsMode]]
                    )

case class OnPremise(
                      hosts: Option[List[Option[String]]],
                      port: Option[Int],
                      subnetId: Option[String],
                      tlsMode: Option[List[TlsMode]]
                    )

case class OnPremise(
                      httpPort: Option[Int],
                      nativePort: Option[Int],
                      shards: Option[List[Shards]],
                      tlsMode: Option[List[TlsMode]]
                    )

case class OperationProfiling(
                               mode: Option[String],
                               slowOpThreshold: Option[Int]
                             )

case class Options(
                    allowedHttpMethods: Option[List[Option[String]]],
                    browserCacheSettings: Option[Int],
                    cacheHttpHeaders: Option[List[Option[String]]],
                    cors: Option[List[Option[String]]],
                    customHostHeader: Option[String],
                    customServerName: Option[String],
                    disableCache: Option[Boolean],
                    disableProxyForceRanges: Option[Boolean],
                    edgeCacheSettings: Option[Int],
                    fetchedCompressed: Option[Boolean],
                    forwardHostHeader: Option[Boolean],
                    gzipOn: Option[Boolean],
                    ignoreCookie: Option[Boolean],
                    ignoreQueryParams: Option[Boolean],
                    proxyCacheMethodsSet: Option[Boolean],
                    queryParamsBlacklist: Option[List[Option[String]]],
                    queryParamsWhitelist: Option[List[Option[String]]],
                    redirectHttpToHttps: Option[Boolean],
                    redirectHttpsToHttp: Option[Boolean],
                    slice: Option[Boolean],
                    staticRequestHeaders: Option[List[Option[String]]],
                    staticResponseHeaders: Option[Map[String, Option[String]]]
                  )

case class Origin(
                   backup: Option[Boolean],
                   enabled: Option[Boolean],
                   originGroupId: Option[Int],
                   source: String
                 )

case class Package(
                    bucketName: String,
                    objectName: String,
                    sha256: Option[String]
                  )

case class Parameters(
                       custom: Option[List[Custom]],
                       description: Option[String],
                       hidden: Option[Boolean],
                       id: Option[String],
                       labelValues: Option[List[LabelValues]],
                       text: Option[List[Text]],
                       title: Option[String]
                     )

case class Parameters(
                       custom: Option[List[Custom]],
                       description: Option[String],
                       hidden: Option[Boolean],
                       id: String,
                       labelValues: Option[List[LabelValues]],
                       text: Option[List[Text]],
                       title: Option[String]
                     )

case class Parametrization(
                            parameters: Option[List[Parameters]],
                            selectors: Option[String]
                          )

case class Password(
                     raw: Option[String]
                   )

case class Path(
                 exact: Option[String],
                 prefix: Option[String],
                 regex: Option[String]
               )

case class Pattern(
                    function: Option[String],
                    regexp: Option[String],
                    retention: Option[List[Retention]]
                  )

case class Pattern(
                    function: String,
                    regexp: Option[String],
                    retention: Option[List[Retention]]
                  )

case class PerformanceDiagnostics(
                                   enabled: Option[Boolean],
                                   sessionsSamplingInterval: Int,
                                   statementsSamplingInterval: Int
                                 )

case class PerformanceDiagnostics(
                                   enabled: Option[Boolean],
                                   sessionsSamplingInterval: Option[Int],
                                   statementsSamplingInterval: Option[Int]
                                 )

case class Permission(
                       databaseName: Option[String]
                     )

case class Permission(
                       databaseName: Option[String],
                       roles: Option[List[Option[String]]]
                     )

case class Permission(
                       databaseName: Option[String],
                       roles: Option[Set[Option[String]]]
                     )

case class Permission(
                       databaseName: String
                     )

case class Permission(
                       databaseName: String,
                       roles: Option[List[Option[String]]]
                     )

case class Permission(
                       databaseName: String,
                       roles: Option[Set[Option[String]]]
                     )

case class Permission(
                       role: String,
                       topicName: String
                     )

case class PlacementPolicy(
                            hostAffinityRules: Option[List[HostAffinityRules]],
                            placementGroupId: Option[String]
                          )

case class PlacementPolicy(
                            placementGroupId: String
                          )

case class Policy(
                   tag: Option[String],
                   zoneInstancesLimit: Option[Int],
                   zoneRequestsLimit: Option[Int]
                 )

case class Policy(
                   tag: String,
                   zoneInstancesLimit: Option[Int],
                   zoneRequestsLimit: Option[Int]
                 )

case class PoolerConfig(
                         poolClientIdleTimeout: Option[Int],
                         poolSize: Option[Int],
                         poolingMode: Option[String]
                       )

case class PoolerConfig(
                         poolDiscard: Option[Boolean],
                         poolingMode: Option[String]
                       )

case class Position(
                     h: Option[Int],
                     w: Option[Int],
                     x: Option[Int],
                     y: Option[Int]
                   )

case class PostgresSource(
                           connection: Option[List[Connection]],
                           database: Option[String],
                           excludeTables: Option[List[Option[String]]],
                           includeTables: Option[List[Option[String]]],
                           objectTransferSettings: Option[List[ObjectTransferSettings]],
                           password: Option[List[Password]],
                           securityGroups: Option[List[Option[String]]],
                           serviceSchema: Option[String],
                           slotGigabyteLagLimit: Option[Int],
                           user: Option[String]
                         )

case class PostgresTarget(
                           connection: Option[List[Connection]],
                           database: Option[String],
                           password: Option[List[Password]],
                           securityGroups: Option[List[Option[String]]],
                           user: Option[String]
                         )

case class Principals(
                       andPrincipals: List[AndPrincipals]
                     )

case class Principals(
                       andPrincipals: Option[List[AndPrincipals]]
                     )

case class PrivateKeyLockboxSecret(
                                    id: String,
                                    key: String
                                  )

case class Provider(
                     cloudId: Option[String],
                     endpoint: Option[String],
                     folderId: Option[String],
                     insecure: Option[Boolean],
                     maxRetries: Option[Int],
                     organizationId: Option[String],
                     plaintext: Option[Boolean],
                     regionId: Option[String],
                     serviceAccountKeyFile: Option[String],
                     storageAccessKey: Option[String],
                     storageEndpoint: Option[String],
                     storageSecretKey: Option[String],
                     token: Option[String],
                     ymqAccessKey: Option[String],
                     ymqEndpoint: Option[String],
                     ymqSecretKey: Option[String],
                     zone: Option[String]
                   )

case class Queries(
                    downsampling: Option[List[Downsampling]],
                    target: Option[List[Target]]
                  )

case class Quota(
                  errors: Option[Int],
                  executionTime: Option[Int],
                  intervalDuration: Int,
                  queries: Option[Int],
                  readRows: Option[Int],
                  resultRows: Option[Int]
                )

case class Quota(
                  errors: Option[Int],
                  executionTime: Option[Int],
                  intervalDuration: Option[Int],
                  queries: Option[Int],
                  readRows: Option[Int],
                  resultRows: Option[Int]
                )

case class Rabbitmq(
                     password: Option[String],
                     username: Option[String],
                     vhost: Option[String]
                   )

case class Rbac(
                 action: Option[String],
                 principals: List[Principals]
               )

case class Rbac(
                 action: Option[String],
                 principals: Option[List[Principals]]
               )

case class RedirectAction(
                           removeQuery: Option[Boolean],
                           replaceHost: Option[String],
                           replacePath: Option[String],
                           replacePort: Option[Int],
                           replacePrefix: Option[String],
                           replaceScheme: Option[String],
                           responseCode: Option[String]
                         )

case class Redirects(
                      httpToHttps: Option[Boolean]
                    )

case class Region(
                   id: Option[String]
                 )

case class Region(
                   id: String
                 )

case class Regional(
                     location: Option[List[Location]],
                     region: String
                   )

case class Regional(
                     region: Option[String]
                   )

case class Resources(
                      coreFraction: Option[Int],
                      cores: Int,
                      gpus: Option[Int],
                      memory: Double
                    )

case class Resources(
                      coreFraction: Option[Int],
                      cores: Option[Int],
                      gpus: Option[Int],
                      memory: Option[Double]
                    )

case class Resources(
                      diskSize: Int,
                      diskTypeId: Option[String],
                      resourcePresetId: String
                    )

case class Resources(
                      diskSize: Int,
                      diskTypeId: String,
                      resourcePresetId: String
                    )

case class Resources(
                      diskSize: Option[Int],
                      diskTypeId: Option[String],
                      resourcePresetId: Option[String]
                    )

case class ResourcesMongocfg(
                              diskSize: Int,
                              diskTypeId: String,
                              resourcePresetId: String
                            )

case class ResourcesMongocfg(
                              diskSize: Option[Int],
                              diskTypeId: Option[String],
                              resourcePresetId: Option[String]
                            )

case class ResourcesMongod(
                            diskSize: Int,
                            diskTypeId: String,
                            resourcePresetId: String
                          )

case class ResourcesMongod(
                            diskSize: Option[Int],
                            diskTypeId: Option[String],
                            resourcePresetId: Option[String]
                          )

case class ResourcesMongoinfra(
                                diskSize: Int,
                                diskTypeId: String,
                                resourcePresetId: String
                              )

case class ResourcesMongoinfra(
                                diskSize: Option[Int],
                                diskTypeId: Option[String],
                                resourcePresetId: Option[String]
                              )

case class ResourcesMongos(
                            diskSize: Int,
                            diskTypeId: String,
                            resourcePresetId: String
                          )

case class ResourcesMongos(
                            diskSize: Option[Int],
                            diskTypeId: Option[String],
                            resourcePresetId: Option[String]
                          )

case class Restore(
                    backupId: Option[String],
                    time: Option[String]
                  )

case class Restore(
                    backupId: String,
                    time: Option[String]
                  )

case class Restore(
                    backupId: String,
                    time: Option[String],
                    timeInclusive: Option[Boolean]
                  )

case class Retention(
                      age: Int,
                      precision: Int
                    )

case class Retention(
                      age: Option[Int],
                      precision: Option[Int]
                    )

case class Right(
                  max: Option[String],
                  min: Option[String],
                  precision: Option[Int],
                  title: Option[String],
                  `type`: Option[String],
                  unitFormat: Option[String]
                )

case class Route(
                  grpcRoute: Option[List[GrpcRoute]],
                  httpRoute: Option[List[HttpRoute]],
                  name: Option[String],
                  routeOptions: Option[List[RouteOptions]]
                )

case class RouteOptions(
                         rbac: Option[List[Rbac]]
                       )

case class Rule(
                 applyServerSideEncryptionByDefault: List[ApplyServerSideEncryptionByDefault]
               )

case class Rule(
                 defaultRetention: List[DefaultRetention]
               )

case class Rule(
                 description: Option[String],
                 expirePeriod: Option[String],
                 retainedTop: Option[Int],
                 tagRegexp: Option[String],
                 untagged: Option[Boolean]
               )

case class S3Connection(
                         bucketName: Option[String],
                         externalS3: Option[List[ExternalS3]]
                       )

case class S3Connection(
                         bucketName: String,
                         externalS3: List[ExternalS3]
                       )

case class ScalePolicy(
                        autoScale: Option[List[AutoScale]],
                        fixedScale: Option[List[FixedScale]]
                      )

case class ScalePolicy(
                        autoScale: Option[List[AutoScale]],
                        fixedScale: Option[List[FixedScale]],
                        testAutoScale: Option[List[TestAutoScale]]
                      )

case class ScalePolicy(
                        fixedScale: List[FixedScale]
                      )

case class ScalePolicy(
                        fixedScale: Option[List[FixedScale]]
                      )

case class SchedulePolicy(
                           expression: Option[String],
                           startAt: Option[String]
                         )

case class SchedulingPolicy(
                             preemptible: Option[Boolean]
                           )

case class SecondaryDisk(
                          autoDelete: Option[Boolean],
                          deviceName: Option[String],
                          diskId: Option[String],
                          mode: Option[String]
                        )

case class SecondaryDisk(
                          autoDelete: Option[Boolean],
                          deviceName: Option[String],
                          diskId: String,
                          mode: Option[String]
                        )

case class SecondaryDisk(
                          deviceName: Option[String],
                          diskId: Option[String],
                          initializeParams: Option[List[InitializeParams]],
                          mode: Option[String]
                        )

case class Secrets(
                    environmentVariable: String,
                    id: String,
                    key: String,
                    versionId: String
                  )

case class Security(
                     enableEncryption: Option[Boolean],
                     kmip: Option[List[Kmip]]
                   )

case class SecuritySettings(
                             encryptedAssertions: Boolean
                           )

case class SecuritySettings(
                             encryptedAssertions: Option[Boolean]
                           )

case class SegmentHosts(
                         fqdn: Option[String]
                       )

case class SegmentSubcluster(
                              resources: List[Resources]
                            )

case class SegmentSubcluster(
                              resources: Option[List[Resources]]
                            )

case class SelfManaged(
                        certificate: String,
                        privateKey: Option[String],
                        privateKeyLockboxSecret: Option[List[PrivateKeyLockboxSecret]]
                      )

case class SeriesOverrides(
                            name: Option[String],
                            settings: Option[List[Settings]],
                            targetIndex: Option[String]
                          )

case class ServerSideEncryptionConfiguration(
                                              rule: List[Rule]
                                            )

case class ServerlessDatabase(
                               enableThrottlingRcuLimit: Option[Boolean],
                               provisionedRcuLimit: Option[Int],
                               storageSizeLimit: Option[Int],
                               throttlingRcuLimit: Option[Int]
                             )

case class SessionAffinity(
                            connection: Option[List[Connection]],
                            cookie: Option[List[Cookie]],
                            header: Option[List[Header]]
                          )

case class SetParameter(
                         auditAuthorizationSuccess: Option[Boolean]
                       )

case class Settings(
                     addHttpCorsHeader: Option[Boolean],
                     allowDdl: Option[Boolean],
                     allowIntrospectionFunctions: Option[Boolean],
                     allowSuspiciousLowCardinalityTypes: Option[Boolean],
                     asyncInsert: Option[Boolean],
                     asyncInsertBusyTimeout: Option[Int],
                     asyncInsertMaxDataSize: Option[Int],
                     asyncInsertStaleTimeout: Option[Int],
                     asyncInsertThreads: Option[Int],
                     cancelHttpReadonlyQueriesOnClientClose: Option[Boolean],
                     compile: Option[Boolean],
                     compileExpressions: Option[Boolean],
                     connectTimeout: Option[Int],
                     connectTimeoutWithFailover: Option[Int],
                     countDistinctImplementation: Option[String],
                     distinctOverflowMode: Option[String],
                     distributedAggregationMemoryEfficient: Option[Boolean],
                     distributedDdlTaskTimeout: Option[Int],
                     distributedProductMode: Option[String],
                     emptyResultForAggregationByEmptySet: Option[Boolean],
                     enableHttpCompression: Option[Boolean],
                     fallbackToStaleReplicasForDistributedQueries: Option[Boolean],
                     flattenNested: Option[Boolean],
                     forceIndexByDate: Option[Boolean],
                     forcePrimaryKey: Option[Boolean],
                     groupByOverflowMode: Option[String],
                     groupByTwoLevelThreshold: Option[Int],
                     groupByTwoLevelThresholdBytes: Option[Int],
                     httpConnectionTimeout: Option[Int],
                     httpHeadersProgressInterval: Option[Int],
                     httpReceiveTimeout: Option[Int],
                     httpSendTimeout: Option[Int],
                     inputFormatDefaultsForOmittedFields: Option[Boolean],
                     inputFormatValuesInterpretExpressions: Option[Boolean],
                     insertNullAsDefault: Option[Boolean],
                     insertQuorum: Option[Int],
                     insertQuorumTimeout: Option[Int],
                     joinOverflowMode: Option[String],
                     joinUseNulls: Option[Boolean],
                     joinedSubqueryRequiresAlias: Option[Boolean],
                     lowCardinalityAllowInNativeFormat: Option[Boolean],
                     maxAstDepth: Option[Int],
                     maxAstElements: Option[Int],
                     maxBlockSize: Option[Int],
                     maxBytesBeforeExternalGroupBy: Option[Int],
                     maxBytesBeforeExternalSort: Option[Int],
                     maxBytesInDistinct: Option[Int],
                     maxBytesInJoin: Option[Int],
                     maxBytesInSet: Option[Int],
                     maxBytesToRead: Option[Int],
                     maxBytesToSort: Option[Int],
                     maxBytesToTransfer: Option[Int],
                     maxColumnsToRead: Option[Int],
                     maxConcurrentQueriesForUser: Option[Int],
                     maxExecutionTime: Option[Int],
                     maxExpandedAstElements: Option[Int],
                     maxHttpGetRedirects: Option[Int],
                     maxInsertBlockSize: Option[Int],
                     maxMemoryUsage: Option[Int],
                     maxMemoryUsageForUser: Option[Int],
                     maxNetworkBandwidth: Option[Int],
                     maxNetworkBandwidthForUser: Option[Int],
                     maxQuerySize: Option[Int],
                     maxReplicaDelayForDistributedQueries: Option[Int],
                     maxResultBytes: Option[Int],
                     maxResultRows: Option[Int],
                     maxRowsInDistinct: Option[Int],
                     maxRowsInJoin: Option[Int],
                     maxRowsInSet: Option[Int],
                     maxRowsToGroupBy: Option[Int],
                     maxRowsToRead: Option[Int],
                     maxRowsToSort: Option[Int],
                     maxRowsToTransfer: Option[Int],
                     maxTemporaryColumns: Option[Int],
                     maxTemporaryNonConstColumns: Option[Int],
                     maxThreads: Option[Int],
                     memoryProfilerSampleProbability: Option[Double],
                     memoryProfilerStep: Option[Int],
                     mergeTreeMaxBytesToUseCache: Option[Int],
                     mergeTreeMaxRowsToUseCache: Option[Int],
                     mergeTreeMinBytesForConcurrentRead: Option[Int],
                     mergeTreeMinRowsForConcurrentRead: Option[Int],
                     minBytesToUseDirectIo: Option[Int],
                     minCountToCompile: Option[Int],
                     minCountToCompileExpression: Option[Int],
                     minExecutionSpeed: Option[Int],
                     minExecutionSpeedBytes: Option[Int],
                     minInsertBlockSizeBytes: Option[Int],
                     minInsertBlockSizeRows: Option[Int],
                     outputFormatJsonQuote64bitIntegers: Option[Boolean],
                     outputFormatJsonQuoteDenormals: Option[Boolean],
                     priority: Option[Int],
                     quotaMode: Option[String],
                     readOverflowMode: Option[String],
                     readonly: Option[Int],
                     receiveTimeout: Option[Int],
                     replicationAlterPartitionsSync: Option[Int],
                     resultOverflowMode: Option[String],
                     selectSequentialConsistency: Option[Boolean],
                     sendProgressInHttpHeaders: Option[Boolean],
                     sendTimeout: Option[Int],
                     setOverflowMode: Option[String],
                     skipUnavailableShards: Option[Boolean],
                     sortOverflowMode: Option[String],
                     timeoutBeforeCheckingExecutionSpeed: Option[Int],
                     timeoutOverflowMode: Option[String],
                     transferOverflowMode: Option[String],
                     transformNullIn: Option[Boolean],
                     useUncompressedCache: Option[Boolean],
                     waitForAsyncInsert: Option[Boolean],
                     waitForAsyncInsertTimeout: Option[Int]
                   )

case class Settings(
                     clickhouseSource: Option[List[ClickhouseSource]],
                     clickhouseTarget: Option[List[ClickhouseTarget]],
                     mongoSource: Option[List[MongoSource]],
                     mongoTarget: Option[List[MongoTarget]],
                     mysqlSource: Option[List[MysqlSource]],
                     mysqlTarget: Option[List[MysqlTarget]],
                     postgresSource: Option[List[PostgresSource]],
                     postgresTarget: Option[List[PostgresTarget]]
                   )

case class Settings(
                     color: Option[String],
                     growDown: Option[Boolean],
                     name: Option[String],
                     stackName: Option[String],
                     `type`: Option[String],
                     yaxisPosition: Option[String]
                   )

case class Settings(
                     saslMechanism: Option[String],
                     saslPassword: Option[String],
                     saslUsername: Option[String],
                     securityProtocol: Option[String]
                   )

case class Shard(
                  name: Option[String],
                  resources: Option[List[Resources]],
                  weight: Option[Int]
                )

case class Shard(
                  name: String,
                  resources: Option[List[Resources]],
                  weight: Option[Int]
                )

case class ShardGroup(
                       description: Option[String],
                       name: Option[String],
                       shardNames: Option[List[Option[String]]]
                     )

case class ShardGroup(
                       description: Option[String],
                       name: String,
                       shardNames: List[Option[String]]
                     )

case class Sharding(
                     columnValueHash: Option[List[ColumnValueHash]],
                     transferId: Option[List[TransferId]]
                   )

case class Shards(
                   hosts: Option[List[Option[String]]],
                   name: Option[String]
                 )

case class SharedEgressGateway(

                              )

case class SnapshotSpec(
                         description: Option[String],
                         labels: Option[Map[String, Option[String]]]
                       )

case class SniHandler(
                       handler: List[Handler],
                       name: String,
                       serverNames: Set[Option[String]]
                     )

case class SniHandler(
                       handler: Option[List[Handler]],
                       name: Option[String],
                       serverNames: Option[Set[Option[String]]]
                     )

case class SourceCluster(
                          alias: Option[String],
                          externalCluster: Option[List[ExternalCluster]],
                          thisCluster: Option[List[ThisCluster]]
                        )

case class SslCertificate(
                           certificateManagerId: Option[String],
                           status: Option[String],
                           `type`: String
                         )

case class Standard(

                   )

case class StaticRoute(
                        destinationPrefix: Option[String],
                        gatewayId: Option[String],
                        nextHopAddress: Option[String]
                      )

case class Storage(
                    journal: Option[List[Journal]],
                    wiredTiger: Option[List[WiredTiger]]
                  )

case class Storage(
                    wiredTiger: Option[List[WiredTiger]]
                  )

case class StorageConfig(
                          groupCount: Int,
                          storageTypeId: String
                        )

case class StorageConfig(
                          groupCount: Option[Int],
                          storageTypeId: Option[String]
                        )

case class Stream(
                   handler: Option[List[Handler]]
                 )

case class StreamBackend(
                          enableProxyProtocol: Option[Boolean],
                          healthcheck: Option[List[Healthcheck]],
                          loadBalancingConfig: Option[List[LoadBalancingConfig]],
                          name: Option[String],
                          port: Option[Int],
                          targetGroupIds: Option[List[Option[String]]],
                          tls: Option[List[Tls]],
                          weight: Option[Int]
                        )

case class StreamBackend(
                          enableProxyProtocol: Option[Boolean],
                          healthcheck: Option[List[Healthcheck]],
                          loadBalancingConfig: Option[List[LoadBalancingConfig]],
                          name: String,
                          port: Option[Int],
                          targetGroupIds: List[Option[String]],
                          tls: Option[List[Tls]],
                          weight: Option[Int]
                        )

case class StreamHandler(
                          backendGroupId: Option[String]
                        )

case class StreamHealthcheck(
                              receive: Option[String],
                              send: Option[String]
                            )

case class SubclusterSpec(
                           assignPublicIp: Option[Boolean],
                           autoscalingConfig: Option[List[AutoscalingConfig]],
                           hostsCount: Int,
                           id: Option[String],
                           name: String,
                           resources: List[Resources],
                           role: String,
                           subnetId: String
                         )

case class SubclusterSpec(
                           assignPublicIp: Option[Boolean],
                           autoscalingConfig: Option[List[AutoscalingConfig]],
                           hostsCount: Option[Int],
                           id: Option[String],
                           name: Option[String],
                           resources: Option[List[Resources]],
                           role: Option[String],
                           subnetId: Option[String]
                         )

case class Target(
                   address: Option[String],
                   subnetId: Option[String]
                 )

case class Target(
                   address: String,
                   subnetId: String
                 )

case class Target(
                   hidden: Option[Boolean],
                   query: Option[String],
                   textMode: Option[Boolean]
                 )

case class Target(
                   ipAddress: String,
                   privateIpv4Address: Option[Boolean],
                   subnetId: Option[String]
                 )

case class TargetCluster(
                          alias: Option[String],
                          externalCluster: Option[List[ExternalCluster]],
                          thisCluster: Option[List[ThisCluster]]
                        )

case class TcpOptions(
                       port: Int
                     )

case class TcpOptions(
                       port: Option[Int]
                     )

case class TestAutoScale(
                          cpuUtilizationTarget: Option[Double],
                          customRule: Option[List[CustomRule]],
                          initialSize: Int,
                          maxSize: Option[Int],
                          measurementDuration: Int,
                          minZoneSize: Option[Int],
                          stabilizationDuration: Option[Int],
                          warmupDuration: Option[Int]
                        )

case class TestAutoScale(
                          cpuUtilizationTarget: Option[Double],
                          customRule: Option[List[CustomRule]],
                          initialSize: Option[Int],
                          maxSize: Option[Int],
                          measurementDuration: Option[Int],
                          minZoneSize: Option[Int],
                          stabilizationDuration: Option[Int],
                          warmupDuration: Option[Int]
                        )

case class Text(
                 defaultValue: Option[String]
               )

case class Text(
                 text: Option[String]
               )

case class ThisCluster(

                      )

case class Timer(
                  cronExpression: Option[String]
                )

case class Timer(
                  cronExpression: String
                )

case class Title(
                  size: Option[String],
                  text: Option[String]
                )

case class Title(
                  size: Option[String],
                  text: String
                )

case class Tls(
                defaultHandler: List[DefaultHandler],
                sniHandler: Option[List[SniHandler]]
              )

case class Tls(
                defaultHandler: Option[List[DefaultHandler]],
                sniHandler: Option[List[SniHandler]]
              )

case class Tls(
                sni: Option[String],
                validationContext: Option[List[ValidationContext]]
              )

case class TlsMode(
                    disabled: Option[List[Disabled]],
                    enabled: Option[List[Enabled]]
                  )

case class Topic(
                  clusterId: String,
                  name: String,
                  partitions: Int,
                  replicationFactor: Int,
                  topicConfig: Option[List[TopicConfig]]
                )

case class Topic(
                  name: String,
                  partitions: Int,
                  replicationFactor: Int,
                  topicConfig: Option[List[TopicConfig]]
                )

case class TopicConfig(
                        cleanupPolicy: Option[String],
                        compressionType: Option[String],
                        deleteRetentionMs: Option[String],
                        fileDeleteDelayMs: Option[String],
                        flushMessages: Option[String],
                        flushMs: Option[String],
                        maxMessageBytes: Option[String],
                        minCompactionLagMs: Option[String],
                        minInsyncReplicas: Option[String],
                        preallocate: Option[Boolean],
                        retentionBytes: Option[String],
                        retentionMs: Option[String],
                        segmentBytes: Option[String]
                      )

case class TransferId(

                     )

case class Transition(
                       date: Option[String],
                       days: Option[Int],
                       storageClass: String
                     )

case class User(
                 authenticationPlugin: Option[String],
                 connectionLimits: Option[List[ConnectionLimits]],
                 globalPermissions: Option[Set[Option[String]]],
                 name: Option[String],
                 password: Option[String],
                 permission: Option[Set[Permission]]
               )

case class User(
                 authenticationPlugin: Option[String],
                 connectionLimits: Option[List[ConnectionLimits]],
                 globalPermissions: Option[Set[Option[String]]],
                 name: String,
                 password: String,
                 permission: Option[Set[Permission]]
               )

case class User(
                 connLimit: Option[Int],
                 grants: Option[List[Option[String]]],
                 login: Option[Boolean],
                 name: String,
                 password: String,
                 permission: Option[Set[Permission]],
                 settings: Option[Map[String, Option[String]]]
               )

case class User(
                 connLimit: Option[Int],
                 grants: Option[List[Option[String]]],
                 login: Option[Boolean],
                 name: String,
                 permission: Option[Set[Permission]],
                 settings: Option[Map[String, Option[String]]]
               )

case class User(
                 name: Option[String],
                 password: Option[String],
                 permission: Option[Set[Permission]]
               )

case class User(
                 name: Option[String],
                 password: Option[String],
                 permission: Option[Set[Permission]],
                 quota: Option[Set[Quota]],
                 settings: Option[List[Settings]]
               )

case class User(
                 name: String,
                 password: String,
                 permission: Option[Set[Permission]]
               )

case class User(
                 name: String,
                 password: String,
                 permission: Option[Set[Permission]],
                 quota: Option[Set[Quota]],
                 settings: Option[List[Settings]]
               )

case class ValidationContext(
                              trustedCaBytes: Option[String],
                              trustedCaId: Option[String]
                            )

case class Value(
                  exact: Option[String],
                  prefix: Option[String],
                  regex: Option[String]
                )

case class VersionInfo(
                        currentVersion: Option[String],
                        newRevisionAvailable: Option[Boolean],
                        newRevisionSummary: Option[String],
                        versionDeprecated: Option[Boolean]
                      )

case class Versioning(
                       enabled: Option[Boolean]
                     )

case class VisualizationSettings(
                                  aggregation: Option[String],
                                  colorSchemeSettings: Option[List[ColorSchemeSettings]],
                                  heatmapSettings: Option[List[HeatmapSettings]],
                                  interpolate: Option[String],
                                  normalize: Option[Boolean],
                                  showLabels: Option[Boolean],
                                  title: Option[String],
                                  `type`: Option[String],
                                  yaxisSettings: Option[List[YaxisSettings]]
                                )

case class Website(
                    errorDocument: Option[String],
                    indexDocument: Option[String],
                    redirectAllRequestsTo: Option[String],
                    routingRules: Option[String]
                  )

case class Widgets(
                    chart: Option[List[Chart]],
                    position: Option[List[Position]],
                    text: Option[List[Text]],
                    title: Option[List[Title]]
                  )

case class WiredTiger(
                       blockCompressor: Option[String],
                       cacheSizeGb: Option[Double]
                     )

case class WiredTiger(
                       cacheSizeGb: Option[Double]
                     )

case class YandexAlbBackendGroup(
                                  backendGroupId: Option[String],
                                  createdAt: Option[String],
                                  description: Option[String],
                                  folderId: Option[String],
                                  grpcBackend: Option[List[GrpcBackend]],
                                  httpBackend: Option[List[HttpBackend]],
                                  labels: Option[Map[String, Option[String]]],
                                  name: Option[String],
                                  sessionAffinity: Option[List[SessionAffinity]],
                                  streamBackend: Option[List[StreamBackend]]
                                )

case class YandexAlbBackendGroup(
                                  createdAt: Option[String],
                                  description: Option[String],
                                  folderId: Option[String],
                                  grpcBackend: Option[List[GrpcBackend]],
                                  httpBackend: Option[List[HttpBackend]],
                                  labels: Option[Map[String, Option[String]]],
                                  name: Option[String],
                                  sessionAffinity: Option[List[SessionAffinity]],
                                  streamBackend: Option[List[StreamBackend]]
                                )

case class YandexAlbHttpRouter(
                                createdAt: Option[String],
                                description: Option[String],
                                folderId: Option[String],
                                httpRouterId: Option[String],
                                labels: Option[Map[String, Option[String]]],
                                name: Option[String],
                                routeOptions: Option[List[RouteOptions]]
                              )

case class YandexAlbHttpRouter(
                                createdAt: Option[String],
                                description: Option[String],
                                folderId: Option[String],
                                labels: Option[Map[String, Option[String]]],
                                name: Option[String],
                                routeOptions: Option[List[RouteOptions]]
                              )

case class YandexAlbLoadBalancer(
                                  allocationPolicy: List[AllocationPolicy],
                                  createdAt: Option[String],
                                  description: Option[String],
                                  folderId: Option[String],
                                  labels: Option[Map[String, Option[String]]],
                                  listener: Option[List[Listener]],
                                  logGroupId: Option[String],
                                  logOptions: Option[List[LogOptions]],
                                  name: Option[String],
                                  networkId: String,
                                  regionId: Option[String],
                                  securityGroupIds: Option[Set[Option[String]]],
                                  status: Option[String]
                                )

case class YandexAlbLoadBalancer(
                                  allocationPolicy: Option[List[AllocationPolicy]],
                                  createdAt: Option[String],
                                  description: Option[String],
                                  folderId: Option[String],
                                  labels: Option[Map[String, Option[String]]],
                                  listener: Option[List[Listener]],
                                  loadBalancerId: Option[String],
                                  logGroupId: Option[String],
                                  logOptions: Option[List[LogOptions]],
                                  name: Option[String],
                                  networkId: Option[String],
                                  regionId: Option[String],
                                  securityGroupIds: Option[Set[Option[String]]],
                                  status: Option[String]
                                )

case class YandexAlbTargetGroup(
                                 createdAt: Option[String],
                                 description: Option[String],
                                 folderId: Option[String],
                                 labels: Option[Map[String, Option[String]]],
                                 name: Option[String],
                                 target: Option[List[Target]]
                               )

case class YandexAlbTargetGroup(
                                 createdAt: Option[String],
                                 description: Option[String],
                                 folderId: Option[String],
                                 labels: Option[Map[String, Option[String]]],
                                 name: Option[String],
                                 target: Option[List[Target]],
                                 targetGroupId: Option[String]
                               )

case class YandexAlbVirtualHost(
                                 authority: Option[Set[Option[String]]],
                                 httpRouterId: Option[String],
                                 modifyRequestHeaders: Option[List[ModifyRequestHeaders]],
                                 modifyResponseHeaders: Option[List[ModifyResponseHeaders]],
                                 name: Option[String],
                                 route: Option[List[Route]],
                                 routeOptions: Option[List[RouteOptions]],
                                 virtualHostId: Option[String]
                               )

case class YandexAlbVirtualHost(
                                 authority: Option[Set[Option[String]]],
                                 httpRouterId: String,
                                 modifyRequestHeaders: Option[List[ModifyRequestHeaders]],
                                 modifyResponseHeaders: Option[List[ModifyResponseHeaders]],
                                 name: String,
                                 route: Option[List[Route]],
                                 routeOptions: Option[List[RouteOptions]]
                               )

case class YandexApiGateway(
                             apiGatewayId: Option[String],
                             connectivity: Option[List[Connectivity]],
                             createdAt: Option[String],
                             customDomains: Option[Set[CustomDomains]],
                             description: Option[String],
                             domain: Option[String],
                             folderId: Option[String],
                             labels: Option[Map[String, Option[String]]],
                             logGroupId: Option[String],
                             name: Option[String],
                             status: Option[String],
                             userDomains: Option[Set[Option[String]]]
                           )

case class YandexApiGateway(
                             connectivity: Option[List[Connectivity]],
                             createdAt: Option[String],
                             customDomains: Option[Set[CustomDomains]],
                             description: Option[String],
                             domain: Option[String],
                             folderId: Option[String],
                             labels: Option[Map[String, Option[String]]],
                             logGroupId: Option[String],
                             name: String,
                             spec: String,
                             status: Option[String],
                             userDomains: Option[Set[Option[String]]]
                           )

case class YandexBillingCloudBinding(
                                      billingAccountId: String,
                                      cloudId: String
                                    )

case class YandexCdnOriginGroup(
                                 folderId: Option[String],
                                 name: Option[String],
                                 origin: Option[Set[Origin]],
                                 originGroupId: Option[Int],
                                 useNext: Option[Boolean]
                               )

case class YandexCdnOriginGroup(
                                 folderId: Option[String],
                                 name: String,
                                 origin: Set[Origin],
                                 useNext: Option[Boolean]
                               )

case class YandexCdnResource(
                              active: Option[Boolean],
                              cname: Option[String],
                              createdAt: Option[String],
                              folderId: Option[String],
                              options: Option[List[Options]],
                              originGroupId: Option[Int],
                              originGroupName: Option[String],
                              originProtocol: Option[String],
                              resourceId: Option[String],
                              secondaryHostnames: Option[Set[Option[String]]],
                              sslCertificate: Option[Set[SslCertificate]],
                              updatedAt: Option[String]
                            )

case class YandexCdnResource(
                              active: Option[Boolean],
                              cname: Option[String],
                              createdAt: Option[String],
                              folderId: Option[String],
                              options: Option[List[Options]],
                              originGroupId: Option[Int],
                              originGroupName: Option[String],
                              originProtocol: Option[String],
                              secondaryHostnames: Option[Set[Option[String]]],
                              sslCertificate: Option[Set[SslCertificate]],
                              updatedAt: Option[String]
                            )

case class YandexClientConfig(
                               cloudId: Option[String],
                               folderId: Option[String],
                               iamToken: Option[String],
                               zone: Option[String]
                             )

case class YandexCmCertificate(
                                certificateId: Option[String],
                                challenges: Option[List[Challenges]],
                                createdAt: Option[String],
                                description: Option[String],
                                domains: Option[List[Option[String]]],
                                folderId: Option[String],
                                issuedAt: Option[String],
                                issuer: Option[String],
                                labels: Option[Map[String, Option[String]]],
                                name: Option[String],
                                notAfter: Option[String],
                                notBefore: Option[String],
                                serial: Option[String],
                                status: Option[String],
                                subject: Option[String],
                                `type`: Option[String],
                                updatedAt: Option[String],
                                waitValidation: Option[Boolean]
                              )

case class YandexCmCertificate(
                                challenges: Option[List[Challenges]],
                                createdAt: Option[String],
                                deletionProtection: Option[Boolean],
                                description: Option[String],
                                domains: Option[List[Option[String]]],
                                folderId: Option[String],
                                issuedAt: Option[String],
                                issuer: Option[String],
                                labels: Option[Map[String, Option[String]]],
                                managed: Option[List[Managed]],
                                name: String,
                                notAfter: Option[String],
                                notBefore: Option[String],
                                selfManaged: Option[List[SelfManaged]],
                                serial: Option[String],
                                status: Option[String],
                                subject: Option[String],
                                `type`: Option[String],
                                updatedAt: Option[String]
                              )

case class YandexCmCertificateContent(
                                       certificateId: Option[String],
                                       certificates: Option[List[Option[String]]],
                                       folderId: Option[String],
                                       name: Option[String],
                                       privateKey: Option[String],
                                       privateKeyFormat: Option[String],
                                       waitValidation: Option[Boolean]
                                     )

case class YandexComputeDisk(
                              allowRecreate: Option[Boolean],
                              blockSize: Option[Int],
                              createdAt: Option[String],
                              description: Option[String],
                              diskPlacementPolicy: Option[List[DiskPlacementPolicy]],
                              folderId: Option[String],
                              imageId: Option[String],
                              labels: Option[Map[String, Option[String]]],
                              name: Option[String],
                              productIds: Option[List[Option[String]]],
                              size: Option[Int],
                              snapshotId: Option[String],
                              status: Option[String],
                              `type`: Option[String],
                              zone: Option[String]
                            )

case class YandexComputeDisk(
                              blockSize: Option[Int],
                              createdAt: Option[String],
                              description: Option[String],
                              diskId: Option[String],
                              diskPlacementPolicy: Option[List[DiskPlacementPolicy]],
                              folderId: Option[String],
                              imageId: Option[String],
                              instanceIds: Option[List[Option[String]]],
                              labels: Option[Map[String, Option[String]]],
                              name: Option[String],
                              productIds: Option[List[Option[String]]],
                              size: Option[Int],
                              snapshotId: Option[String],
                              status: Option[String],
                              `type`: Option[String],
                              zone: Option[String]
                            )

case class YandexComputeDiskPlacementGroup(
                                            createdAt: Option[String],
                                            description: Option[String],
                                            folderId: Option[String],
                                            groupId: Option[String],
                                            labels: Option[Map[String, Option[String]]],
                                            name: Option[String],
                                            status: Option[String],
                                            zone: Option[String]
                                          )

case class YandexComputeDiskPlacementGroup(
                                            createdAt: Option[String],
                                            description: Option[String],
                                            folderId: Option[String],
                                            labels: Option[Map[String, Option[String]]],
                                            name: Option[String],
                                            status: Option[String],
                                            zone: Option[String]
                                          )

case class YandexComputeFilesystem(
                                    blockSize: Option[Int],
                                    createdAt: Option[String],
                                    description: Option[String],
                                    filesystemId: Option[String],
                                    folderId: Option[String],
                                    labels: Option[Map[String, Option[String]]],
                                    name: Option[String],
                                    size: Option[Int],
                                    status: Option[String],
                                    `type`: Option[String],
                                    zone: Option[String]
                                  )

case class YandexComputeFilesystem(
                                    blockSize: Option[Int],
                                    createdAt: Option[String],
                                    description: Option[String],
                                    folderId: Option[String],
                                    labels: Option[Map[String, Option[String]]],
                                    name: Option[String],
                                    size: Option[Int],
                                    status: Option[String],
                                    `type`: Option[String],
                                    zone: Option[String]
                                  )

case class YandexComputeGpuCluster(
                                    createdAt: Option[String],
                                    description: Option[String],
                                    folderId: Option[String],
                                    gpuClusterId: Option[String],
                                    interconnectType: Option[String],
                                    labels: Option[Map[String, Option[String]]],
                                    name: Option[String],
                                    status: Option[String],
                                    zone: Option[String]
                                  )

case class YandexComputeGpuCluster(
                                    createdAt: Option[String],
                                    description: Option[String],
                                    folderId: Option[String],
                                    interconnectType: Option[String],
                                    labels: Option[Map[String, Option[String]]],
                                    name: Option[String],
                                    status: Option[String],
                                    zone: Option[String]
                                  )

case class YandexComputeImage(
                               createdAt: Option[String],
                               description: Option[String],
                               family: Option[String],
                               folderId: Option[String],
                               imageId: Option[String],
                               labels: Option[Map[String, Option[String]]],
                               minDiskSize: Option[Int],
                               name: Option[String],
                               osType: Option[String],
                               pooled: Option[Boolean],
                               productIds: Option[Set[Option[String]]],
                               size: Option[Int],
                               status: Option[String]
                             )

case class YandexComputeImage(
                               createdAt: Option[String],
                               description: Option[String],
                               family: Option[String],
                               folderId: Option[String],
                               labels: Option[Map[String, Option[String]]],
                               minDiskSize: Option[Int],
                               name: Option[String],
                               osType: Option[String],
                               pooled: Option[Boolean],
                               productIds: Option[Set[Option[String]]],
                               size: Option[Int],
                               sourceDisk: Option[String],
                               sourceFamily: Option[String],
                               sourceImage: Option[String],
                               sourceSnapshot: Option[String],
                               sourceUrl: Option[String],
                               status: Option[String]
                             )

case class YandexComputeInstance(
                                  allowRecreate: Option[Boolean],
                                  allowStoppingForUpdate: Option[Boolean],
                                  bootDisk: List[BootDisk],
                                  createdAt: Option[String],
                                  description: Option[String],
                                  filesystem: Option[Set[Filesystem]],
                                  folderId: Option[String],
                                  fqdn: Option[String],
                                  gpuClusterId: Option[String],
                                  hostname: Option[String],
                                  labels: Option[Map[String, Option[String]]],
                                  localDisk: Option[List[LocalDisk]],
                                  metadata: Option[Map[String, Option[String]]],
                                  metadataOptions: Option[List[MetadataOptions]],
                                  name: Option[String],
                                  networkAccelerationType: Option[String],
                                  networkInterface: List[NetworkInterface],
                                  placementPolicy: Option[List[PlacementPolicy]],
                                  platformId: Option[String],
                                  resources: List[Resources],
                                  schedulingPolicy: Option[List[SchedulingPolicy]],
                                  secondaryDisk: Option[List[SecondaryDisk]],
                                  serviceAccountId: Option[String],
                                  status: Option[String],
                                  zone: Option[String]
                                )

case class YandexComputeInstance(
                                  bootDisk: Option[List[BootDisk]],
                                  createdAt: Option[String],
                                  description: Option[String],
                                  filesystem: Option[Set[Filesystem]],
                                  folderId: Option[String],
                                  fqdn: Option[String],
                                  gpuClusterId: Option[String],
                                  instanceId: Option[String],
                                  labels: Option[Map[String, Option[String]]],
                                  localDisk: Option[List[LocalDisk]],
                                  metadata: Option[Map[String, Option[String]]],
                                  metadataOptions: Option[List[MetadataOptions]],
                                  name: Option[String],
                                  networkAccelerationType: Option[String],
                                  networkInterface: Option[List[NetworkInterface]],
                                  placementPolicy: Option[List[PlacementPolicy]],
                                  platformId: Option[String],
                                  resources: Option[List[Resources]],
                                  schedulingPolicy: Option[List[SchedulingPolicy]],
                                  secondaryDisk: Option[List[SecondaryDisk]],
                                  serviceAccountId: Option[String],
                                  status: Option[String],
                                  zone: Option[String]
                                )

case class YandexComputeInstanceGroup(
                                       allocationPolicy: List[AllocationPolicy],
                                       applicationLoadBalancer: Option[List[ApplicationLoadBalancer]],
                                       createdAt: Option[String],
                                       deletionProtection: Option[Boolean],
                                       deployPolicy: List[DeployPolicy],
                                       description: Option[String],
                                       folderId: Option[String],
                                       healthCheck: Option[List[HealthCheck]],
                                       instanceTemplate: List[InstanceTemplate],
                                       instances: Option[List[Instances]],
                                       labels: Option[Map[String, Option[String]]],
                                       loadBalancer: Option[List[LoadBalancer]],
                                       maxCheckingHealthDuration: Option[Int],
                                       name: Option[String],
                                       scalePolicy: List[ScalePolicy],
                                       serviceAccountId: String,
                                       status: Option[String],
                                       variables: Option[Map[String, Option[String]]]
                                     )

case class YandexComputeInstanceGroup(
                                       allocationPolicy: Option[List[AllocationPolicy]],
                                       applicationBalancerState: Option[List[ApplicationBalancerState]],
                                       applicationLoadBalancer: Option[List[ApplicationLoadBalancer]],
                                       createdAt: Option[String],
                                       deletionProtection: Option[Boolean],
                                       deployPolicy: Option[List[DeployPolicy]],
                                       description: Option[String],
                                       folderId: Option[String],
                                       healthCheck: Option[List[HealthCheck]],
                                       instanceGroupId: String,
                                       instanceTemplate: Option[List[InstanceTemplate]],
                                       instances: Option[List[Instances]],
                                       labels: Option[Map[String, Option[String]]],
                                       loadBalancer: Option[List[LoadBalancer]],
                                       loadBalancerState: Option[List[LoadBalancerState]],
                                       maxCheckingHealthDuration: Option[Int],
                                       name: Option[String],
                                       scalePolicy: Option[List[ScalePolicy]],
                                       serviceAccountId: Option[String],
                                       status: Option[String],
                                       variables: Option[Map[String, Option[String]]]
                                     )

case class YandexComputePlacementGroup(
                                        createdAt: Option[String],
                                        description: Option[String],
                                        folderId: Option[String],
                                        groupId: Option[String],
                                        labels: Option[Map[String, Option[String]]],
                                        name: Option[String]
                                      )

case class YandexComputePlacementGroup(
                                        createdAt: Option[String],
                                        description: Option[String],
                                        folderId: Option[String],
                                        labels: Option[Map[String, Option[String]]],
                                        name: Option[String]
                                      )

case class YandexComputeSnapshot(
                                  createdAt: Option[String],
                                  description: Option[String],
                                  diskSize: Option[Int],
                                  folderId: Option[String],
                                  labels: Option[Map[String, Option[String]]],
                                  name: Option[String],
                                  productIds: Option[List[Option[String]]],
                                  snapshotId: Option[String],
                                  sourceDiskId: Option[String],
                                  status: Option[String],
                                  storageSize: Option[Int]
                                )

case class YandexComputeSnapshot(
                                  createdAt: Option[String],
                                  description: Option[String],
                                  diskSize: Option[Int],
                                  folderId: Option[String],
                                  labels: Option[Map[String, Option[String]]],
                                  name: Option[String],
                                  sourceDiskId: String,
                                  storageSize: Option[Int]
                                )

case class YandexComputeSnapshotSchedule(
                                          createdAt: Option[String],
                                          description: Option[String],
                                          diskIds: Option[List[Option[String]]],
                                          folderId: Option[String],
                                          labels: Option[Map[String, Option[String]]],
                                          name: Option[String],
                                          retentionPeriod: Option[String],
                                          schedulePolicy: Option[List[SchedulePolicy]],
                                          snapshotCount: Option[Int],
                                          snapshotScheduleId: Option[String],
                                          snapshotSpec: Option[List[SnapshotSpec]],
                                          status: Option[String]
                                        )

case class YandexComputeSnapshotSchedule(
                                          createdAt: Option[String],
                                          description: Option[String],
                                          diskIds: Option[List[Option[String]]],
                                          folderId: Option[String],
                                          labels: Option[Map[String, Option[String]]],
                                          name: Option[String],
                                          retentionPeriod: Option[String],
                                          schedulePolicy: Option[List[SchedulePolicy]],
                                          snapshotCount: Option[Int],
                                          snapshotSpec: Option[List[SnapshotSpec]],
                                          status: Option[String]
                                        )

case class YandexContainerRegistry(
                                    createdAt: Option[String],
                                    folderId: Option[String],
                                    labels: Option[Map[String, Option[String]]],
                                    name: Option[String],
                                    registryId: Option[String],
                                    status: Option[String]
                                  )

case class YandexContainerRegistry(
                                    createdAt: Option[String],
                                    folderId: Option[String],
                                    labels: Option[Map[String, Option[String]]],
                                    name: Option[String],
                                    status: Option[String]
                                  )

case class YandexContainerRegistryIamBinding(
                                              members: Set[Option[String]],
                                              registryId: String,
                                              role: String,
                                              sleepAfter: Option[Int]
                                            )

case class YandexContainerRegistryIpPermission(
                                                pull: Option[Set[Option[String]]],
                                                push: Option[Set[Option[String]]],
                                                registryId: Option[String],
                                                registryName: Option[String]
                                              )

case class YandexContainerRegistryIpPermission(
                                                pull: Option[Set[Option[String]]],
                                                push: Option[Set[Option[String]]],
                                                registryId: String
                                              )

case class YandexContainerRepository(
                                      name: Option[String],
                                      repositoryId: Option[String]
                                    )

case class YandexContainerRepository(
                                      name: String
                                    )

case class YandexContainerRepositoryIamBinding(
                                                members: Set[Option[String]],
                                                repositoryId: String,
                                                role: String,
                                                sleepAfter: Option[Int]
                                              )

case class YandexContainerRepositoryLifecyclePolicy(
                                                     createdAt: Option[String],
                                                     description: Option[String],
                                                     lifecyclePolicyId: Option[String],
                                                     name: Option[String],
                                                     repositoryId: Option[String],
                                                     rule: Option[List[Rule]],
                                                     status: Option[String]
                                                   )

case class YandexContainerRepositoryLifecyclePolicy(
                                                     createdAt: Option[String],
                                                     description: Option[String],
                                                     name: Option[String],
                                                     repositoryId: String,
                                                     rule: Option[List[Rule]],
                                                     status: String
                                                   )

case class YandexDataprocCluster(
                                  bucket: Option[String],
                                  clusterConfig: List[ClusterConfig],
                                  createdAt: Option[String],
                                  deletionProtection: Option[Boolean],
                                  description: Option[String],
                                  folderId: Option[String],
                                  hostGroupIds: Option[Set[Option[String]]],
                                  labels: Option[Map[String, Option[String]]],
                                  name: String,
                                  securityGroupIds: Option[Set[Option[String]]],
                                  serviceAccountId: String,
                                  uiProxy: Option[Boolean],
                                  zoneId: Option[String]
                                )

case class YandexDataprocCluster(
                                  bucket: Option[String],
                                  clusterConfig: Option[List[ClusterConfig]],
                                  clusterId: Option[String],
                                  createdAt: Option[String],
                                  deletionProtection: Option[Boolean],
                                  description: Option[String],
                                  folderId: Option[String],
                                  hostGroupIds: Option[Set[Option[String]]],
                                  labels: Option[Map[String, Option[String]]],
                                  name: Option[String],
                                  securityGroupIds: Option[Set[Option[String]]],
                                  serviceAccountId: Option[String],
                                  uiProxy: Option[Boolean],
                                  zoneId: Option[String]
                                )

case class YandexDatatransferEndpoint(
                                       description: Option[String],
                                       folderId: Option[String],
                                       labels: Option[Map[String, Option[String]]],
                                       name: Option[String],
                                       settings: Option[List[Settings]]
                                     )

case class YandexDatatransferTransfer(
                                       description: Option[String],
                                       folderId: Option[String],
                                       labels: Option[Map[String, Option[String]]],
                                       name: Option[String],
                                       onCreateActivateMode: Option[String],
                                       sourceId: Option[String],
                                       targetId: Option[String],
                                       `type`: Option[String],
                                       warning: Option[String]
                                     )

case class YandexDnsRecordset(
                               data: Set[Option[String]],
                               name: String,
                               ttl: Int,
                               `type`: String,
                               zoneId: String
                             )

case class YandexDnsZone(
                          createdAt: Option[String],
                          description: Option[String],
                          dnsZoneId: Option[String],
                          folderId: Option[String],
                          labels: Option[Map[String, Option[String]]],
                          name: Option[String],
                          privateNetworks: Option[Set[Option[String]]],
                          public: Option[Boolean],
                          zone: Option[String]
                        )

case class YandexDnsZone(
                          createdAt: Option[String],
                          description: Option[String],
                          folderId: Option[String],
                          labels: Option[Map[String, Option[String]]],
                          name: Option[String],
                          privateNetworks: Option[Set[Option[String]]],
                          public: Option[Boolean],
                          zone: String
                        )

case class YandexFunction(
                           connectivity: Option[List[Connectivity]],
                           content: Option[List[Content]],
                           createdAt: Option[String],
                           description: Option[String],
                           entrypoint: String,
                           environment: Option[Map[String, Option[String]]],
                           executionTimeout: Option[String],
                           folderId: Option[String],
                           imageSize: Option[Int],
                           labels: Option[Map[String, Option[String]]],
                           loggroupId: Option[String],
                           memory: Int,
                           name: String,
                           `package`: Option[List[Package]],
                           runtime: String,
                           secrets: Option[List[Secrets]],
                           serviceAccountId: Option[String],
                           tags: Option[Set[Option[String]]],
                           userHash: String,
                           version: Option[String]
                         )

case class YandexFunction(
                           connectivity: Option[List[Connectivity]],
                           createdAt: Option[String],
                           description: Option[String],
                           entrypoint: Option[String],
                           environment: Option[Map[String, Option[String]]],
                           executionTimeout: Option[String],
                           folderId: Option[String],
                           functionId: Option[String],
                           imageSize: Option[Int],
                           labels: Option[Map[String, Option[String]]],
                           loggroupId: Option[String],
                           memory: Option[Int],
                           name: Option[String],
                           runtime: Option[String],
                           secrets: Option[List[Secrets]],
                           serviceAccountId: Option[String],
                           tags: Option[Set[Option[String]]],
                           version: Option[String]
                         )

case class YandexFunctionIamBinding(
                                     functionId: String,
                                     members: Set[Option[String]],
                                     role: String,
                                     sleepAfter: Option[Int]
                                   )

case class YandexFunctionScalingPolicy(
                                        functionId: String,
                                        policy: Option[List[Policy]]
                                      )

case class YandexFunctionScalingPolicy(
                                        functionId: String,
                                        policy: Option[Set[Policy]]
                                      )

case class YandexFunctionTrigger(
                                  container: Option[List[Container]],
                                  createdAt: Option[String],
                                  description: Option[String],
                                  dlq: Option[List[Dlq]],
                                  folderId: Option[String],
                                  function: Option[List[Function]],
                                  iot: Option[List[Iot]],
                                  labels: Option[Map[String, Option[String]]],
                                  logGroup: Option[List[LogGroup]],
                                  logging: Option[List[Logging]],
                                  messageQueue: Option[List[MessageQueue]],
                                  name: Option[String],
                                  objectStorage: Option[List[ObjectStorage]],
                                  timer: Option[List[Timer]],
                                  triggerId: Option[String]
                                )

case class YandexFunctionTrigger(
                                  container: Option[List[Container]],
                                  createdAt: Option[String],
                                  description: Option[String],
                                  dlq: Option[List[Dlq]],
                                  folderId: Option[String],
                                  function: Option[List[Function]],
                                  iot: Option[List[Iot]],
                                  labels: Option[Map[String, Option[String]]],
                                  logGroup: Option[List[LogGroup]],
                                  logging: Option[List[Logging]],
                                  messageQueue: Option[List[MessageQueue]],
                                  name: String,
                                  objectStorage: Option[List[ObjectStorage]],
                                  timer: Option[List[Timer]]
                                )

case class YandexIamPolicy(
                            binding: Set[Binding],
                            policyData: Option[String]
                          )

case class YandexIamRole(
                          description: Option[String],
                          roleId: Option[String]
                        )

case class YandexIamServiceAccount(
                                    createdAt: Option[String],
                                    description: Option[String],
                                    folderId: Option[String],
                                    name: Option[String],
                                    serviceAccountId: Option[String]
                                  )

case class YandexIamServiceAccount(
                                    createdAt: Option[String],
                                    description: Option[String],
                                    folderId: Option[String],
                                    name: String
                                  )

case class YandexIamServiceAccountApiKey(
                                          createdAt: Option[String],
                                          description: Option[String],
                                          encryptedSecretKey: Option[String],
                                          keyFingerprint: Option[String],
                                          pgpKey: Option[String],
                                          secretKey: Option[String],
                                          serviceAccountId: String
                                        )

case class YandexIamServiceAccountIamBinding(
                                              members: Set[Option[String]],
                                              role: String,
                                              serviceAccountId: String,
                                              sleepAfter: Option[Int]
                                            )

case class YandexIamServiceAccountIamMember(
                                             member: String,
                                             role: String,
                                             serviceAccountId: String,
                                             sleepAfter: Option[Int]
                                           )

case class YandexIamServiceAccountIamPolicy(
                                             policyData: String,
                                             serviceAccountId: String
                                           )

case class YandexIamServiceAccountKey(
                                       createdAt: Option[String],
                                       description: Option[String],
                                       encryptedPrivateKey: Option[String],
                                       format: Option[String],
                                       keyAlgorithm: Option[String],
                                       keyFingerprint: Option[String],
                                       pgpKey: Option[String],
                                       privateKey: Option[String],
                                       publicKey: Option[String],
                                       serviceAccountId: String
                                     )

case class YandexIamServiceAccountStaticAccessKey(
                                                   accessKey: Option[String],
                                                   createdAt: Option[String],
                                                   description: Option[String],
                                                   encryptedSecretKey: Option[String],
                                                   keyFingerprint: Option[String],
                                                   pgpKey: Option[String],
                                                   secretKey: Option[String],
                                                   serviceAccountId: String
                                                 )

case class YandexIamUser(
                          defaultEmail: Option[String],
                          login: Option[String],
                          userId: Option[String]
                        )

case class YandexIotCoreBroker(
                                brokerId: Option[String],
                                certificates: Option[Set[Option[String]]],
                                createdAt: Option[String],
                                description: Option[String],
                                folderId: Option[String],
                                labels: Option[Map[String, Option[String]]],
                                name: Option[String]
                              )

case class YandexIotCoreBroker(
                                certificates: Option[Set[Option[String]]],
                                createdAt: Option[String],
                                description: Option[String],
                                folderId: Option[String],
                                labels: Option[Map[String, Option[String]]],
                                name: String
                              )

case class YandexIotCoreDevice(
                                aliases: Option[Map[String, Option[String]]],
                                certificates: Option[Set[Option[String]]],
                                createdAt: Option[String],
                                description: Option[String],
                                deviceId: Option[String],
                                name: Option[String],
                                passwords: Option[Set[Option[String]]],
                                registryId: Option[String]
                              )

case class YandexIotCoreDevice(
                                aliases: Option[Map[String, Option[String]]],
                                certificates: Option[Set[Option[String]]],
                                createdAt: Option[String],
                                description: Option[String],
                                name: String,
                                passwords: Option[Set[Option[String]]],
                                registryId: String
                              )

case class YandexIotCoreRegistry(
                                  certificates: Option[Set[Option[String]]],
                                  createdAt: Option[String],
                                  description: Option[String],
                                  folderId: Option[String],
                                  labels: Option[Map[String, Option[String]]],
                                  name: Option[String],
                                  passwords: Option[Set[Option[String]]],
                                  registryId: Option[String]
                                )

case class YandexIotCoreRegistry(
                                  certificates: Option[Set[Option[String]]],
                                  createdAt: Option[String],
                                  description: Option[String],
                                  folderId: Option[String],
                                  labels: Option[Map[String, Option[String]]],
                                  name: String,
                                  passwords: Option[Set[Option[String]]]
                                )

case class YandexKmsSecretCiphertext(
                                      aadContext: Option[String],
                                      ciphertext: Option[String],
                                      keyId: String,
                                      plaintext: String
                                    )

case class YandexKmsSymmetricKey(
                                  createdAt: Option[String],
                                  defaultAlgorithm: Option[String],
                                  description: Option[String],
                                  folderId: Option[String],
                                  labels: Option[Map[String, Option[String]]],
                                  name: Option[String],
                                  rotatedAt: Option[String],
                                  rotationPeriod: Option[String],
                                  status: Option[String]
                                )

case class YandexKmsSymmetricKeyIamBinding(
                                            members: Set[Option[String]],
                                            role: String,
                                            sleepAfter: Option[Int],
                                            symmetricKeyId: String
                                          )

case class YandexKubernetesCluster(
                                    clusterId: Option[String],
                                    clusterIpv4Range: Option[String],
                                    clusterIpv6Range: Option[String],
                                    createdAt: Option[String],
                                    description: Option[String],
                                    folderId: Option[String],
                                    health: Option[String],
                                    kmsProvider: Option[List[KmsProvider]],
                                    labels: Option[Map[String, Option[String]]],
                                    logGroupId: Option[String],
                                    master: Option[List[Master]],
                                    name: Option[String],
                                    networkId: Option[String],
                                    networkImplementation: Option[List[NetworkImplementation]],
                                    networkPolicyProvider: Option[String],
                                    nodeIpv4CidrMaskSize: Option[Int],
                                    nodeServiceAccountId: Option[String],
                                    releaseChannel: Option[String],
                                    serviceAccountId: Option[String],
                                    serviceIpv4Range: Option[String],
                                    serviceIpv6Range: Option[String],
                                    status: Option[String]
                                  )

case class YandexKubernetesCluster(
                                    clusterIpv4Range: Option[String],
                                    clusterIpv6Range: Option[String],
                                    createdAt: Option[String],
                                    description: Option[String],
                                    folderId: Option[String],
                                    health: Option[String],
                                    kmsProvider: Option[List[KmsProvider]],
                                    labels: Option[Map[String, Option[String]]],
                                    logGroupId: Option[String],
                                    master: List[Master],
                                    name: Option[String],
                                    networkId: String,
                                    networkImplementation: Option[List[NetworkImplementation]],
                                    networkPolicyProvider: Option[String],
                                    nodeIpv4CidrMaskSize: Option[Int],
                                    nodeServiceAccountId: String,
                                    releaseChannel: Option[String],
                                    serviceAccountId: String,
                                    serviceIpv4Range: Option[String],
                                    serviceIpv6Range: Option[String],
                                    status: Option[String]
                                  )

case class YandexKubernetesNodeGroup(
                                      allocationPolicy: Option[List[AllocationPolicy]],
                                      allowedUnsafeSysctls: Option[List[Option[String]]],
                                      clusterId: Option[String],
                                      createdAt: Option[String],
                                      deployPolicy: Option[List[DeployPolicy]],
                                      description: Option[String],
                                      folderId: Option[String],
                                      instanceGroupId: Option[String],
                                      instanceTemplate: Option[List[InstanceTemplate]],
                                      labels: Option[Map[String, Option[String]]],
                                      maintenancePolicy: Option[List[MaintenancePolicy]],
                                      name: Option[String],
                                      nodeGroupId: Option[String],
                                      nodeLabels: Option[Map[String, Option[String]]],
                                      nodeTaints: Option[List[Option[String]]],
                                      scalePolicy: Option[List[ScalePolicy]],
                                      status: Option[String],
                                      versionInfo: Option[List[VersionInfo]]
                                    )

case class YandexKubernetesNodeGroup(
                                      allocationPolicy: Option[List[AllocationPolicy]],
                                      allowedUnsafeSysctls: Option[List[Option[String]]],
                                      clusterId: String,
                                      createdAt: Option[String],
                                      deployPolicy: Option[List[DeployPolicy]],
                                      description: Option[String],
                                      instanceGroupId: Option[String],
                                      instanceTemplate: List[InstanceTemplate],
                                      labels: Option[Map[String, Option[String]]],
                                      maintenancePolicy: Option[List[MaintenancePolicy]],
                                      name: Option[String],
                                      nodeLabels: Option[Map[String, Option[String]]],
                                      nodeTaints: Option[List[Option[String]]],
                                      scalePolicy: List[ScalePolicy],
                                      status: Option[String],
                                      version: Option[String],
                                      versionInfo: Option[List[VersionInfo]]
                                    )

case class YandexLbNetworkLoadBalancer(
                                        attachedTargetGroup: Option[Set[AttachedTargetGroup]],
                                        createdAt: Option[String],
                                        deletionProtection: Option[Boolean],
                                        description: Option[String],
                                        folderId: Option[String],
                                        labels: Option[Map[String, Option[String]]],
                                        listener: Option[Set[Listener]],
                                        name: Option[String],
                                        networkLoadBalancerId: Option[String],
                                        regionId: Option[String],
                                        `type`: Option[String]
                                      )

case class YandexLbNetworkLoadBalancer(
                                        attachedTargetGroup: Option[Set[AttachedTargetGroup]],
                                        createdAt: Option[String],
                                        deletionProtection: Option[Boolean],
                                        description: Option[String],
                                        folderId: Option[String],
                                        labels: Option[Map[String, Option[String]]],
                                        listener: Option[Set[Listener]],
                                        name: Option[String],
                                        regionId: Option[String],
                                        `type`: Option[String]
                                      )

case class YandexLbTargetGroup(
                                createdAt: Option[String],
                                description: Option[String],
                                folderId: Option[String],
                                labels: Option[Map[String, Option[String]]],
                                name: Option[String],
                                regionId: Option[String],
                                target: Option[Set[Target]]
                              )

case class YandexLbTargetGroup(
                                createdAt: Option[String],
                                description: Option[String],
                                folderId: Option[String],
                                labels: Option[Map[String, Option[String]]],
                                name: Option[String],
                                target: Option[Set[Target]],
                                targetGroupId: Option[String]
                              )

case class YandexLockboxSecret(
                                createdAt: Option[String],
                                currentVersion: Option[List[CurrentVersion]],
                                deletionProtection: Option[Boolean],
                                description: Option[String],
                                folderId: Option[String],
                                kmsKeyId: Option[String],
                                labels: Option[Map[String, Option[String]]],
                                name: Option[String],
                                secretId: String,
                                status: Option[String]
                              )

case class YandexLockboxSecret(
                                createdAt: Option[String],
                                deletionProtection: Option[Boolean],
                                description: Option[String],
                                folderId: Option[String],
                                kmsKeyId: Option[String],
                                labels: Option[Map[String, Option[String]]],
                                name: Option[String],
                                status: Option[String]
                              )

case class YandexLockboxSecretVersion(
                                       description: Option[String],
                                       entries: List[Entries],
                                       secretId: String
                                     )

case class YandexLockboxSecretVersion(
                                       entries: Option[List[Entries]],
                                       secretId: String,
                                       versionId: Option[String]
                                     )

case class YandexLoggingGroup(
                               cloudId: Option[String],
                               createdAt: Option[String],
                               dataStream: Option[String],
                               description: Option[String],
                               folderId: Option[String],
                               groupId: Option[String],
                               labels: Option[Map[String, Option[String]]],
                               name: Option[String],
                               retentionPeriod: Option[String],
                               status: Option[String]
                             )

case class YandexLoggingGroup(
                               cloudId: Option[String],
                               createdAt: Option[String],
                               dataStream: Option[String],
                               description: Option[String],
                               folderId: Option[String],
                               labels: Option[Map[String, Option[String]]],
                               name: Option[String],
                               retentionPeriod: Option[String],
                               status: Option[String]
                             )

case class YandexMdbClickhouseCluster(
                                       access: Option[List[Access]],
                                       adminPassword: Option[String],
                                       backupWindowStart: Option[List[BackupWindowStart]],
                                       clickhouse: Option[List[Clickhouse]],
                                       cloudStorage: Option[List[CloudStorage]],
                                       clusterId: Option[String],
                                       copySchemaOnNewHosts: Option[Boolean],
                                       createdAt: Option[String],
                                       database: Option[Set[Database]],
                                       deletionProtection: Option[Boolean],
                                       description: Option[String],
                                       embeddedKeeper: Option[Boolean],
                                       environment: Option[String],
                                       folderId: Option[String],
                                       formatSchema: Option[Set[FormatSchema]],
                                       health: Option[String],
                                       host: Option[List[Host]],
                                       labels: Option[Map[String, Option[String]]],
                                       maintenanceWindow: Option[List[MaintenanceWindow]],
                                       mlModel: Option[Set[MlModel]],
                                       name: Option[String],
                                       networkId: Option[String],
                                       securityGroupIds: Option[Set[Option[String]]],
                                       serviceAccountId: Option[String],
                                       shard: Option[Set[Shard]],
                                       shardGroup: Option[List[ShardGroup]],
                                       sqlDatabaseManagement: Option[Boolean],
                                       sqlUserManagement: Option[Boolean],
                                       status: Option[String],
                                       user: Option[Set[User]],
                                       version: Option[String],
                                       zookeeper: Option[List[Zookeeper]]
                                     )

case class YandexMdbClickhouseCluster(
                                       access: Option[List[Access]],
                                       adminPassword: Option[String],
                                       backupWindowStart: Option[List[BackupWindowStart]],
                                       clickhouse: Option[List[Clickhouse]],
                                       cloudStorage: Option[List[CloudStorage]],
                                       clusterId: Option[String],
                                       copySchemaOnNewHosts: Option[Boolean],
                                       createdAt: Option[String],
                                       database: Option[Set[Database]],
                                       deletionProtection: Option[Boolean],
                                       description: Option[String],
                                       embeddedKeeper: Option[Boolean],
                                       environment: String,
                                       folderId: Option[String],
                                       formatSchema: Option[Set[FormatSchema]],
                                       health: Option[String],
                                       host: List[Host],
                                       labels: Option[Map[String, Option[String]]],
                                       maintenanceWindow: Option[List[MaintenanceWindow]],
                                       mlModel: Option[Set[MlModel]],
                                       name: String,
                                       networkId: String,
                                       securityGroupIds: Option[Set[Option[String]]],
                                       serviceAccountId: Option[String],
                                       shard: Option[Set[Shard]],
                                       shardGroup: Option[List[ShardGroup]],
                                       sqlDatabaseManagement: Option[Boolean],
                                       sqlUserManagement: Option[Boolean],
                                       status: Option[String],
                                       user: Option[Set[User]],
                                       version: Option[String],
                                       zookeeper: Option[List[Zookeeper]]
                                     )

case class YandexMdbElasticsearchCluster(
                                          clusterId: Option[String],
                                          config: Option[List[Config]],
                                          createdAt: Option[String],
                                          deletionProtection: Option[Boolean],
                                          description: Option[String],
                                          environment: Option[String],
                                          folderId: Option[String],
                                          health: Option[String],
                                          host: Option[Set[Host]],
                                          labels: Option[Map[String, Option[String]]],
                                          maintenanceWindow: Option[List[MaintenanceWindow]],
                                          name: Option[String],
                                          networkId: Option[String],
                                          securityGroupIds: Option[Set[Option[String]]],
                                          serviceAccountId: Option[String],
                                          status: Option[String]
                                        )

case class YandexMdbElasticsearchCluster(
                                          config: List[Config],
                                          createdAt: Option[String],
                                          deletionProtection: Option[Boolean],
                                          description: Option[String],
                                          environment: String,
                                          folderId: Option[String],
                                          health: Option[String],
                                          host: Option[Set[Host]],
                                          labels: Option[Map[String, Option[String]]],
                                          maintenanceWindow: Option[List[MaintenanceWindow]],
                                          name: String,
                                          networkId: String,
                                          securityGroupIds: Option[Set[Option[String]]],
                                          serviceAccountId: Option[String],
                                          status: Option[String]
                                        )

case class YandexMdbGreenplumCluster(
                                      access: Option[List[Access]],
                                      assignPublicIp: Boolean,
                                      backupWindowStart: Option[List[BackupWindowStart]],
                                      createdAt: Option[String],
                                      deletionProtection: Option[Boolean],
                                      description: Option[String],
                                      environment: String,
                                      folderId: Option[String],
                                      greenplumConfig: Option[Map[String, Option[String]]],
                                      health: Option[String],
                                      labels: Option[Map[String, Option[String]]],
                                      maintenanceWindow: Option[List[MaintenanceWindow]],
                                      masterHostCount: Int,
                                      masterHosts: Option[List[MasterHosts]],
                                      masterSubcluster: List[MasterSubcluster],
                                      name: String,
                                      networkId: String,
                                      poolerConfig: Option[List[PoolerConfig]],
                                      securityGroupIds: Option[Set[Option[String]]],
                                      segmentHostCount: Int,
                                      segmentHosts: Option[List[SegmentHosts]],
                                      segmentInHost: Int,
                                      segmentSubcluster: List[SegmentSubcluster],
                                      status: Option[String],
                                      subnetId: String,
                                      userName: String,
                                      userPassword: String,
                                      version: String,
                                      zone: String
                                    )

case class YandexMdbGreenplumCluster(
                                      access: Option[List[Access]],
                                      assignPublicIp: Option[Boolean],
                                      backupWindowStart: Option[List[BackupWindowStart]],
                                      clusterId: Option[String],
                                      createdAt: Option[String],
                                      deletionProtection: Option[Boolean],
                                      description: Option[String],
                                      environment: Option[String],
                                      folderId: Option[String],
                                      greenplumConfig: Option[Map[String, Option[String]]],
                                      health: Option[String],
                                      labels: Option[Map[String, Option[String]]],
                                      maintenanceWindow: Option[List[MaintenanceWindow]],
                                      masterHostCount: Option[Int],
                                      masterHosts: Option[List[MasterHosts]],
                                      masterSubcluster: Option[List[MasterSubcluster]],
                                      name: Option[String],
                                      networkId: Option[String],
                                      poolerConfig: Option[List[PoolerConfig]],
                                      securityGroupIds: Option[Set[Option[String]]],
                                      segmentHostCount: Option[Int],
                                      segmentHosts: Option[List[SegmentHosts]],
                                      segmentInHost: Option[Int],
                                      segmentSubcluster: Option[List[SegmentSubcluster]],
                                      status: Option[String],
                                      subnetId: Option[String],
                                      userName: Option[String],
                                      version: Option[String],
                                      zone: Option[String]
                                    )

case class YandexMdbKafkaCluster(
                                  access: Option[List[Access]],
                                  clusterId: Option[String],
                                  config: Option[List[Config]],
                                  createdAt: Option[String],
                                  deletionProtection: Option[Boolean],
                                  description: Option[String],
                                  environment: Option[String],
                                  folderId: Option[String],
                                  health: Option[String],
                                  host: Option[Set[Host]],
                                  hostGroupIds: Option[Set[Option[String]]],
                                  labels: Option[Map[String, Option[String]]],
                                  maintenanceWindow: Option[List[MaintenanceWindow]],
                                  name: Option[String],
                                  networkId: Option[String],
                                  securityGroupIds: Option[Set[Option[String]]],
                                  status: Option[String],
                                  subnetIds: Option[List[Option[String]]],
                                  topic: Option[List[Topic]],
                                  user: Option[Set[User]]
                                )

case class YandexMdbKafkaCluster(
                                  config: List[Config],
                                  createdAt: Option[String],
                                  deletionProtection: Option[Boolean],
                                  description: Option[String],
                                  environment: Option[String],
                                  folderId: Option[String],
                                  health: Option[String],
                                  host: Option[Set[Host]],
                                  hostGroupIds: Option[Set[Option[String]]],
                                  labels: Option[Map[String, Option[String]]],
                                  maintenanceWindow: Option[List[MaintenanceWindow]],
                                  name: String,
                                  networkId: String,
                                  securityGroupIds: Option[Set[Option[String]]],
                                  status: Option[String],
                                  subnetIds: Option[List[Option[String]]],
                                  topic: Option[List[Topic]],
                                  user: Option[Set[User]]
                                )

case class YandexMdbKafkaConnector(
                                    clusterId: String,
                                    connectorConfigMirrormaker: Option[List[ConnectorConfigMirrormaker]],
                                    connectorConfigS3Sink: Option[List[ConnectorConfigS3Sink]],
                                    name: String,
                                    properties: Option[Map[String, Option[String]]],
                                    tasksMax: Option[Int]
                                  )

case class YandexMdbKafkaTopic(
                                clusterId: String,
                                name: String,
                                partitions: Int,
                                replicationFactor: Int,
                                topicConfig: Option[List[TopicConfig]]
                              )

case class YandexMdbKafkaTopic(
                                clusterId: String,
                                name: String,
                                partitions: Option[Int],
                                replicationFactor: Option[Int],
                                topicConfig: Option[List[TopicConfig]]
                              )

case class YandexMdbMongodbCluster(
                                    clusterConfig: List[ClusterConfig],
                                    clusterId: Option[String],
                                    createdAt: Option[String],
                                    database: Set[Database],
                                    deletionProtection: Option[Boolean],
                                    description: Option[String],
                                    environment: String,
                                    folderId: Option[String],
                                    health: Option[String],
                                    host: List[Host],
                                    labels: Option[Map[String, Option[String]]],
                                    maintenanceWindow: Option[List[MaintenanceWindow]],
                                    name: String,
                                    networkId: String,
                                    resources: Option[List[Resources]],
                                    resourcesMongocfg: List[ResourcesMongocfg],
                                    resourcesMongod: Option[List[ResourcesMongod]],
                                    resourcesMongoinfra: List[ResourcesMongoinfra],
                                    resourcesMongos: List[ResourcesMongos],
                                    restore: Option[List[Restore]],
                                    securityGroupIds: Option[Set[Option[String]]],
                                    sharded: Option[Boolean],
                                    status: Option[String],
                                    user: Set[User]
                                  )

case class YandexMdbMongodbCluster(
                                    clusterConfig: Option[List[ClusterConfig]],
                                    clusterId: Option[String],
                                    createdAt: Option[String],
                                    database: Option[Set[Database]],
                                    deletionProtection: Option[Boolean],
                                    description: Option[String],
                                    environment: Option[String],
                                    folderId: Option[String],
                                    health: Option[String],
                                    host: Option[List[Host]],
                                    labels: Option[Map[String, Option[String]]],
                                    maintenanceWindow: Option[List[MaintenanceWindow]],
                                    name: Option[String],
                                    networkId: Option[String],
                                    resources: Option[List[Resources]],
                                    resourcesMongocfg: List[ResourcesMongocfg],
                                    resourcesMongod: Option[List[ResourcesMongod]],
                                    resourcesMongoinfra: List[ResourcesMongoinfra],
                                    resourcesMongos: List[ResourcesMongos],
                                    restore: Option[List[Restore]],
                                    securityGroupIds: Option[Set[Option[String]]],
                                    sharded: Option[Boolean],
                                    status: Option[String],
                                    user: Option[Set[User]]
                                  )

case class YandexMdbMysqlCluster(
                                  access: Option[List[Access]],
                                  allowRegenerationHost: Option[Boolean],
                                  backupRetainPeriodDays: Option[Int],
                                  backupWindowStart: Option[List[BackupWindowStart]],
                                  createdAt: Option[String],
                                  database: Option[Set[Database]],
                                  deletionProtection: Option[Boolean],
                                  description: Option[String],
                                  environment: String,
                                  folderId: Option[String],
                                  health: Option[String],
                                  host: List[Host],
                                  hostGroupIds: Option[Set[Option[String]]],
                                  labels: Option[Map[String, Option[String]]],
                                  maintenanceWindow: Option[List[MaintenanceWindow]],
                                  mysqlConfig: Option[Map[String, Option[String]]],
                                  name: String,
                                  networkId: String,
                                  performanceDiagnostics: Option[List[PerformanceDiagnostics]],
                                  resources: List[Resources],
                                  restore: Option[List[Restore]],
                                  securityGroupIds: Option[Set[Option[String]]],
                                  status: Option[String],
                                  user: Option[List[User]],
                                  version: String
                                )

case class YandexMdbMysqlCluster(
                                  access: Option[List[Access]],
                                  backupRetainPeriodDays: Option[Int],
                                  backupWindowStart: Option[List[BackupWindowStart]],
                                  clusterId: Option[String],
                                  createdAt: Option[String],
                                  database: Option[Set[Database]],
                                  deletionProtection: Option[Boolean],
                                  description: Option[String],
                                  environment: Option[String],
                                  folderId: Option[String],
                                  health: Option[String],
                                  host: Option[List[Host]],
                                  hostGroupIds: Option[Set[Option[String]]],
                                  labels: Option[Map[String, Option[String]]],
                                  maintenanceWindow: Option[List[MaintenanceWindow]],
                                  mysqlConfig: Option[Map[String, Option[String]]],
                                  name: Option[String],
                                  networkId: Option[String],
                                  performanceDiagnostics: Option[List[PerformanceDiagnostics]],
                                  resources: Option[List[Resources]],
                                  securityGroupIds: Option[Set[Option[String]]],
                                  status: Option[String],
                                  user: Option[List[User]],
                                  version: Option[String]
                                )

case class YandexMdbMysqlDatabase(
                                   clusterId: String,
                                   name: String
                                 )

case class YandexMdbMysqlUser(
                               authenticationPlugin: Option[String],
                               clusterId: String,
                               connectionLimits: Option[List[ConnectionLimits]],
                               globalPermissions: Option[Set[Option[String]]],
                               name: String,
                               password: String,
                               permission: Option[Set[Permission]]
                             )

case class YandexMdbMysqlUser(
                               authenticationPlugin: Option[String],
                               clusterId: String,
                               connectionLimits: Option[List[ConnectionLimits]],
                               globalPermissions: Option[Set[Option[String]]],
                               name: String,
                               permission: Option[Set[Permission]]
                             )

case class YandexMdbPostgresqlCluster(
                                       clusterId: Option[String],
                                       config: Option[List[Config]],
                                       createdAt: Option[String],
                                       database: Option[Set[Database]],
                                       deletionProtection: Option[Boolean],
                                       description: Option[String],
                                       environment: Option[String],
                                       folderId: Option[String],
                                       health: Option[String],
                                       host: Option[List[Host]],
                                       hostGroupIds: Option[Set[Option[String]]],
                                       labels: Option[Map[String, Option[String]]],
                                       maintenanceWindow: Option[List[MaintenanceWindow]],
                                       name: Option[String],
                                       networkId: Option[String],
                                       securityGroupIds: Option[Set[Option[String]]],
                                       status: Option[String],
                                       user: Option[List[User]]
                                     )

case class YandexMdbPostgresqlCluster(
                                       config: List[Config],
                                       createdAt: Option[String],
                                       database: Option[List[Database]],
                                       deletionProtection: Option[Boolean],
                                       description: Option[String],
                                       environment: String,
                                       folderId: Option[String],
                                       health: Option[String],
                                       host: List[Host],
                                       hostGroupIds: Option[Set[Option[String]]],
                                       hostMasterName: Option[String],
                                       labels: Option[Map[String, Option[String]]],
                                       maintenanceWindow: Option[List[MaintenanceWindow]],
                                       name: String,
                                       networkId: String,
                                       restore: Option[List[Restore]],
                                       securityGroupIds: Option[Set[Option[String]]],
                                       status: Option[String],
                                       user: Option[List[User]]
                                     )

case class YandexMdbPostgresqlDatabase(
                                        clusterId: String,
                                        deletionProtection: Option[String],
                                        extension: Option[Set[Extension]],
                                        lcCollate: Option[String],
                                        lcType: Option[String],
                                        name: String,
                                        owner: Option[String],
                                        templateDb: Option[String]
                                      )

case class YandexMdbPostgresqlDatabase(
                                        clusterId: String,
                                        deletionProtection: Option[String],
                                        extension: Option[Set[Extension]],
                                        lcCollate: Option[String],
                                        lcType: Option[String],
                                        name: String,
                                        owner: String,
                                        templateDb: Option[String]
                                      )

case class YandexMdbPostgresqlUser(
                                    clusterId: String,
                                    connLimit: Option[Int],
                                    deletionProtection: Option[String],
                                    grants: Option[List[Option[String]]],
                                    login: Option[Boolean],
                                    name: String,
                                    password: Option[String],
                                    permission: Option[Set[Permission]],
                                    settings: Option[Map[String, Option[String]]]
                                  )

case class YandexMdbPostgresqlUser(
                                    clusterId: String,
                                    connLimit: Option[Int],
                                    deletionProtection: Option[String],
                                    grants: Option[List[Option[String]]],
                                    login: Option[Boolean],
                                    name: String,
                                    password: String,
                                    permission: Option[Set[Permission]],
                                    settings: Option[Map[String, Option[String]]]
                                  )

case class YandexMdbRedisCluster(
                                  clusterId: Option[String],
                                  config: Option[List[Config]],
                                  createdAt: Option[String],
                                  deletionProtection: Option[Boolean],
                                  description: Option[String],
                                  environment: Option[String],
                                  folderId: Option[String],
                                  health: Option[String],
                                  host: Option[List[Host]],
                                  labels: Option[Map[String, Option[String]]],
                                  maintenanceWindow: Option[List[MaintenanceWindow]],
                                  name: Option[String],
                                  networkId: Option[String],
                                  persistenceMode: Option[String],
                                  resources: Option[List[Resources]],
                                  securityGroupIds: Option[Set[Option[String]]],
                                  sharded: Option[Boolean],
                                  status: Option[String],
                                  tlsEnabled: Option[Boolean]
                                )

case class YandexMdbRedisCluster(
                                  config: List[Config],
                                  createdAt: Option[String],
                                  deletionProtection: Option[Boolean],
                                  description: Option[String],
                                  environment: String,
                                  folderId: Option[String],
                                  health: Option[String],
                                  host: List[Host],
                                  labels: Option[Map[String, Option[String]]],
                                  maintenanceWindow: Option[List[MaintenanceWindow]],
                                  name: String,
                                  networkId: String,
                                  persistenceMode: Option[String],
                                  resources: List[Resources],
                                  securityGroupIds: Option[Set[Option[String]]],
                                  sharded: Option[Boolean],
                                  status: Option[String],
                                  tlsEnabled: Option[Boolean]
                                )

case class YandexMdbSqlserverCluster(
                                      backupWindowStart: Option[List[BackupWindowStart]],
                                      clusterId: Option[String],
                                      createdAt: Option[String],
                                      database: Option[List[Database]],
                                      deletionProtection: Option[Boolean],
                                      description: Option[String],
                                      environment: Option[String],
                                      folderId: Option[String],
                                      health: Option[String],
                                      host: Option[List[Host]],
                                      hostGroupIds: Option[Set[Option[String]]],
                                      labels: Option[Map[String, Option[String]]],
                                      name: Option[String],
                                      networkId: Option[String],
                                      resources: Option[List[Resources]],
                                      securityGroupIds: Option[Set[Option[String]]],
                                      sqlcollation: Option[String],
                                      sqlserverConfig: Option[Map[String, Option[String]]],
                                      status: Option[String],
                                      user: Option[List[User]],
                                      version: Option[String]
                                    )

case class YandexMdbSqlserverCluster(
                                      backupWindowStart: Option[List[BackupWindowStart]],
                                      createdAt: Option[String],
                                      database: List[Database],
                                      deletionProtection: Option[Boolean],
                                      description: Option[String],
                                      environment: String,
                                      folderId: Option[String],
                                      health: Option[String],
                                      host: List[Host],
                                      hostGroupIds: Option[Set[Option[String]]],
                                      labels: Option[Map[String, Option[String]]],
                                      name: String,
                                      networkId: String,
                                      resources: List[Resources],
                                      securityGroupIds: Option[Set[Option[String]]],
                                      sqlcollation: Option[String],
                                      sqlserverConfig: Option[Map[String, Option[String]]],
                                      status: Option[String],
                                      user: List[User],
                                      version: String
                                    )

case class YandexMessageQueue(
                               accessKey: Option[String],
                               arn: Option[String],
                               contentBasedDeduplication: Option[Boolean],
                               delaySeconds: Option[Int],
                               fifoQueue: Option[Boolean],
                               maxMessageSize: Option[Int],
                               messageRetentionSeconds: Option[Int],
                               name: Option[String],
                               namePrefix: Option[String],
                               receiveWaitTimeSeconds: Option[Int],
                               redrivePolicy: Option[String],
                               regionId: Option[String],
                               secretKey: Option[String],
                               visibilityTimeoutSeconds: Option[Int]
                             )

case class YandexMessageQueue(
                               accessKey: Option[String],
                               arn: Option[String],
                               name: String,
                               regionId: Option[String],
                               secretKey: Option[String],
                               url: Option[String]
                             )

case class YandexMonitoringDashboard(
                                      dashboardId: Option[String],
                                      description: Option[String],
                                      folderId: Option[String],
                                      labels: Option[Map[String, Option[String]]],
                                      name: Option[String],
                                      parametrization: Option[List[Parametrization]],
                                      title: Option[String],
                                      widgets: Option[List[Widgets]]
                                    )

case class YandexMonitoringDashboard(
                                      dashboardId: Option[String],
                                      description: Option[String],
                                      folderId: Option[String],
                                      labels: Option[Map[String, Option[String]]],
                                      name: String,
                                      parametrization: Option[List[Parametrization]],
                                      title: Option[String],
                                      widgets: Option[List[Widgets]]
                                    )

case class YandexOrganizationmanagerGroup(
                                           createdAt: Option[String],
                                           description: Option[String],
                                           groupId: Option[String],
                                           members: Option[List[Members]],
                                           name: Option[String],
                                           organizationId: Option[String]
                                         )

case class YandexOrganizationmanagerGroup(
                                           createdAt: Option[String],
                                           description: Option[String],
                                           name: String,
                                           organizationId: String
                                         )

case class YandexOrganizationmanagerGroupIamMember(
                                                    groupId: String,
                                                    member: String,
                                                    role: String,
                                                    sleepAfter: Option[Int]
                                                  )

case class YandexOrganizationmanagerGroupMembership(
                                                     groupId: String,
                                                     members: Set[Option[String]]
                                                   )

case class YandexOrganizationmanagerOrganizationIamBinding(
                                                            members: Set[Option[String]],
                                                            organizationId: String,
                                                            role: String,
                                                            sleepAfter: Option[Int]
                                                          )

case class YandexOrganizationmanagerOrganizationIamMember(
                                                           member: String,
                                                           organizationId: String,
                                                           role: String,
                                                           sleepAfter: Option[Int]
                                                         )

case class YandexOrganizationmanagerSamlFederation(
                                                    autoCreateAccountOnLogin: Option[Boolean],
                                                    caseInsensitiveNameIds: Option[Boolean],
                                                    cookieMaxAge: Option[String],
                                                    createdAt: Option[String],
                                                    description: Option[String],
                                                    federationId: Option[String],
                                                    issuer: Option[String],
                                                    labels: Option[Map[String, Option[String]]],
                                                    name: Option[String],
                                                    organizationId: Option[String],
                                                    securitySettings: Option[List[SecuritySettings]],
                                                    ssoBinding: Option[String],
                                                    ssoUrl: Option[String]
                                                  )

case class YandexOrganizationmanagerSamlFederation(
                                                    autoCreateAccountOnLogin: Option[Boolean],
                                                    caseInsensitiveNameIds: Option[Boolean],
                                                    cookieMaxAge: Option[String],
                                                    createdAt: Option[String],
                                                    description: Option[String],
                                                    issuer: String,
                                                    labels: Option[Map[String, Option[String]]],
                                                    name: String,
                                                    organizationId: String,
                                                    securitySettings: Option[List[SecuritySettings]],
                                                    ssoBinding: String,
                                                    ssoUrl: String
                                                  )

case class YandexOrganizationmanagerSamlFederationUserAccount(
                                                               federationId: String,
                                                               nameId: String
                                                             )

case class YandexResourcemanagerCloud(
                                       cloudId: Option[String],
                                       createdAt: Option[String],
                                       description: Option[String],
                                       name: Option[String]
                                     )

case class YandexResourcemanagerCloud(
                                       createdAt: Option[String],
                                       description: Option[String],
                                       labels: Option[Map[String, Option[String]]],
                                       name: Option[String],
                                       organizationId: Option[String]
                                     )

case class YandexResourcemanagerCloudIamBinding(
                                                 cloudId: String,
                                                 members: Set[Option[String]],
                                                 role: String,
                                                 sleepAfter: Option[Int]
                                               )

case class YandexResourcemanagerCloudIamMember(
                                                cloudId: String,
                                                member: String,
                                                role: String,
                                                sleepAfter: Option[Int]
                                              )

case class YandexResourcemanagerFolder(
                                        cloudId: Option[String],
                                        createdAt: Option[String],
                                        description: Option[String],
                                        folderId: Option[String],
                                        labels: Option[Map[String, Option[String]]],
                                        name: Option[String],
                                        status: Option[String]
                                      )

case class YandexResourcemanagerFolder(
                                        cloudId: Option[String],
                                        createdAt: Option[String],
                                        description: Option[String],
                                        labels: Option[Map[String, Option[String]]],
                                        name: Option[String]
                                      )

case class YandexResourcemanagerFolderIamBinding(
                                                  folderId: String,
                                                  members: Set[Option[String]],
                                                  role: String,
                                                  sleepAfter: Option[Int]
                                                )

case class YandexResourcemanagerFolderIamMember(
                                                 folderId: String,
                                                 member: String,
                                                 role: String,
                                                 sleepAfter: Option[Int]
                                               )

case class YandexResourcemanagerFolderIamPolicy(
                                                 folderId: String,
                                                 policyData: String
                                               )

case class YandexServerlessContainer(
                                      concurrency: Option[Int],
                                      connectivity: Option[List[Connectivity]],
                                      containerId: Option[String],
                                      coreFraction: Option[Int],
                                      cores: Option[Int],
                                      createdAt: Option[String],
                                      description: Option[String],
                                      executionTimeout: Option[String],
                                      folderId: Option[String],
                                      image: Option[List[Image]],
                                      labels: Option[Map[String, Option[String]]],
                                      memory: Option[Int],
                                      name: Option[String],
                                      revisionId: Option[String],
                                      secrets: Option[List[Secrets]],
                                      serviceAccountId: Option[String],
                                      url: Option[String]
                                    )

case class YandexServerlessContainer(
                                      concurrency: Option[Int],
                                      connectivity: Option[List[Connectivity]],
                                      coreFraction: Option[Int],
                                      cores: Option[Int],
                                      createdAt: Option[String],
                                      description: Option[String],
                                      executionTimeout: Option[String],
                                      folderId: Option[String],
                                      image: List[Image],
                                      labels: Option[Map[String, Option[String]]],
                                      memory: Int,
                                      name: String,
                                      revisionId: Option[String],
                                      secrets: Option[List[Secrets]],
                                      serviceAccountId: Option[String],
                                      url: Option[String]
                                    )

case class YandexServerlessContainerIamBinding(
                                                containerId: String,
                                                members: Set[Option[String]],
                                                role: String,
                                                sleepAfter: Option[Int]
                                              )

case class YandexStorageBucket(
                                accessKey: Option[String],
                                acl: Option[String],
                                anonymousAccessFlags: Option[Set[AnonymousAccessFlags]],
                                bucket: Option[String],
                                bucketDomainName: Option[String],
                                bucketPrefix: Option[String],
                                corsRule: Option[List[CorsRule]],
                                defaultStorageClass: Option[String],
                                folderId: Option[String],
                                forceDestroy: Option[Boolean],
                                grant: Option[Set[Grant]],
                                https: Option[Set[Https]],
                                lifecycleRule: Option[List[LifecycleRule]],
                                logging: Option[Set[Logging]],
                                maxSize: Option[Int],
                                objectLockConfiguration: Option[List[ObjectLockConfiguration]],
                                policy: Option[String],
                                secretKey: Option[String],
                                serverSideEncryptionConfiguration: Option[List[ServerSideEncryptionConfiguration]],
                                versioning: Option[List[Versioning]],
                                website: Option[List[Website]],
                                websiteDomain: Option[String],
                                websiteEndpoint: Option[String]
                              )

case class YandexStorageObject(
                                accessKey: Option[String],
                                acl: Option[String],
                                bucket: String,
                                content: Option[String],
                                contentBase64: Option[String],
                                contentType: Option[String],
                                key: String,
                                objectLockLegalHoldStatus: Option[String],
                                objectLockMode: String,
                                objectLockRetainUntilDate: String,
                                secretKey: Option[String],
                                source: Option[String]
                              )

case class YandexVpcAddress(
                             addressId: Option[String],
                             createdAt: Option[String],
                             deletionProtection: Option[Boolean],
                             description: Option[String],
                             externalIpv4Address: Option[List[ExternalIpv4Address]],
                             folderId: Option[String],
                             labels: Option[Map[String, Option[String]]],
                             name: Option[String],
                             reserved: Option[Boolean],
                             used: Option[Boolean]
                           )

case class YandexVpcAddress(
                             createdAt: Option[String],
                             deletionProtection: Option[Boolean],
                             description: Option[String],
                             externalIpv4Address: Option[List[ExternalIpv4Address]],
                             folderId: Option[String],
                             labels: Option[Map[String, Option[String]]],
                             name: Option[String],
                             reserved: Option[Boolean],
                             used: Option[Boolean]
                           )

case class YandexVpcDefaultSecurityGroup(
                                          createdAt: Option[String],
                                          description: Option[String],
                                          egress: Option[Set[Egress]],
                                          folderId: Option[String],
                                          ingress: Option[Set[Ingress]],
                                          labels: Option[Map[String, Option[String]]],
                                          name: Option[String],
                                          networkId: String,
                                          status: Option[String]
                                        )

case class YandexVpcGateway(
                             createdAt: Option[String],
                             description: Option[String],
                             folderId: Option[String],
                             gatewayId: Option[String],
                             labels: Option[Map[String, Option[String]]],
                             name: Option[String],
                             sharedEgressGateway: Option[List[SharedEgressGateway]]
                           )

case class YandexVpcGateway(
                             createdAt: Option[String],
                             description: Option[String],
                             folderId: Option[String],
                             labels: Option[Map[String, Option[String]]],
                             name: Option[String],
                             sharedEgressGateway: Option[List[SharedEgressGateway]]
                           )

case class YandexVpcNetwork(
                             createdAt: Option[String],
                             defaultSecurityGroupId: Option[String],
                             description: Option[String],
                             folderId: Option[String],
                             labels: Option[Map[String, Option[String]]],
                             name: Option[String],
                             networkId: Option[String],
                             subnetIds: Option[List[Option[String]]]
                           )

case class YandexVpcNetwork(
                             createdAt: Option[String],
                             defaultSecurityGroupId: Option[String],
                             description: Option[String],
                             folderId: Option[String],
                             labels: Option[Map[String, Option[String]]],
                             name: Option[String],
                             subnetIds: Option[List[Option[String]]]
                           )

case class YandexVpcRouteTable(
                                createdAt: Option[String],
                                description: Option[String],
                                folderId: Option[String],
                                labels: Option[Map[String, Option[String]]],
                                name: Option[String],
                                networkId: Option[String],
                                routeTableId: Option[String],
                                staticRoute: Option[Set[StaticRoute]]
                              )

case class YandexVpcRouteTable(
                                createdAt: Option[String],
                                description: Option[String],
                                folderId: Option[String],
                                labels: Option[Map[String, Option[String]]],
                                name: Option[String],
                                networkId: String,
                                staticRoute: Option[Set[StaticRoute]]
                              )

case class YandexVpcSecurityGroup(
                                   createdAt: Option[String],
                                   description: Option[String],
                                   egress: Option[Set[Egress]],
                                   folderId: Option[String],
                                   ingress: Option[Set[Ingress]],
                                   labels: Option[Map[String, Option[String]]],
                                   name: Option[String],
                                   networkId: Option[String],
                                   securityGroupId: Option[String],
                                   status: Option[String]
                                 )

case class YandexVpcSecurityGroup(
                                   createdAt: Option[String],
                                   description: Option[String],
                                   egress: Option[Set[Egress]],
                                   folderId: Option[String],
                                   ingress: Option[Set[Ingress]],
                                   labels: Option[Map[String, Option[String]]],
                                   name: Option[String],
                                   networkId: String,
                                   status: Option[String]
                                 )

case class YandexVpcSecurityGroupRule(
                                       description: Option[String],
                                       direction: Option[String],
                                       fromPort: Option[Int],
                                       labels: Option[Map[String, Option[String]]],
                                       port: Option[Int],
                                       predefinedTarget: Option[String],
                                       protocol: Option[String],
                                       ruleId: String,
                                       securityGroupBinding: String,
                                       securityGroupId: Option[String],
                                       toPort: Option[Int],
                                       v4CidrBlocks: Option[List[Option[String]]],
                                       v6CidrBlocks: Option[List[Option[String]]]
                                     )

case class YandexVpcSecurityGroupRule(
                                       description: Option[String],
                                       direction: String,
                                       fromPort: Option[Int],
                                       labels: Option[Map[String, Option[String]]],
                                       port: Option[Int],
                                       predefinedTarget: Option[String],
                                       protocol: Option[String],
                                       securityGroupBinding: String,
                                       securityGroupId: Option[String],
                                       toPort: Option[Int],
                                       v4CidrBlocks: Option[List[Option[String]]],
                                       v6CidrBlocks: Option[List[Option[String]]]
                                     )

case class YandexVpcSubnet(
                            createdAt: Option[String],
                            description: Option[String],
                            dhcpOptions: Option[List[DhcpOptions]],
                            folderId: Option[String],
                            labels: Option[Map[String, Option[String]]],
                            name: Option[String],
                            networkId: Option[String],
                            routeTableId: Option[String],
                            subnetId: Option[String],
                            v4CidrBlocks: Option[List[Option[String]]],
                            v6CidrBlocks: Option[List[Option[String]]],
                            zone: Option[String]
                          )

case class YandexVpcSubnet(
                            createdAt: Option[String],
                            description: Option[String],
                            dhcpOptions: Option[List[DhcpOptions]],
                            folderId: Option[String],
                            labels: Option[Map[String, Option[String]]],
                            name: Option[String],
                            networkId: String,
                            routeTableId: Option[String],
                            v4CidrBlocks: List[Option[String]],
                            v6CidrBlocks: Option[List[Option[String]]],
                            zone: Option[String]
                          )

case class YandexYdbDatabaseDedicated(
                                       assignPublicIps: Option[Boolean],
                                       createdAt: Option[String],
                                       databaseId: Option[String],
                                       databasePath: Option[String],
                                       deletionProtection: Option[Boolean],
                                       description: Option[String],
                                       folderId: Option[String],
                                       labels: Option[Map[String, Option[String]]],
                                       location: Option[List[Location]],
                                       locationId: Option[String],
                                       name: Option[String],
                                       networkId: Option[String],
                                       resourcePresetId: Option[String],
                                       scalePolicy: Option[List[ScalePolicy]],
                                       status: Option[String],
                                       storageConfig: Option[List[StorageConfig]],
                                       subnetIds: Option[Set[Option[String]]],
                                       tlsEnabled: Option[Boolean],
                                       ydbApiEndpoint: Option[String],
                                       ydbFullEndpoint: Option[String]
                                     )

case class YandexYdbDatabaseDedicated(
                                       assignPublicIps: Option[Boolean],
                                       createdAt: Option[String],
                                       databasePath: Option[String],
                                       deletionProtection: Option[Boolean],
                                       description: Option[String],
                                       folderId: Option[String],
                                       labels: Option[Map[String, Option[String]]],
                                       location: Option[List[Location]],
                                       locationId: Option[String],
                                       name: String,
                                       networkId: String,
                                       resourcePresetId: String,
                                       scalePolicy: List[ScalePolicy],
                                       status: Option[String],
                                       storageConfig: List[StorageConfig],
                                       subnetIds: Set[Option[String]],
                                       tlsEnabled: Option[Boolean],
                                       ydbApiEndpoint: Option[String],
                                       ydbFullEndpoint: Option[String]
                                     )

case class YandexYdbDatabaseIamBinding(
                                        databaseId: String,
                                        members: Set[Option[String]],
                                        role: String,
                                        sleepAfter: Option[Int]
                                      )

case class YandexYdbDatabaseServerless(
                                        createdAt: Option[String],
                                        databaseId: Option[String],
                                        databasePath: Option[String],
                                        deletionProtection: Option[Boolean],
                                        description: Option[String],
                                        documentApiEndpoint: Option[String],
                                        folderId: Option[String],
                                        labels: Option[Map[String, Option[String]]],
                                        locationId: Option[String],
                                        name: Option[String],
                                        serverlessDatabase: Option[Set[ServerlessDatabase]],
                                        status: Option[String],
                                        tlsEnabled: Option[Boolean],
                                        ydbApiEndpoint: Option[String],
                                        ydbFullEndpoint: Option[String]
                                      )

case class YandexYdbDatabaseServerless(
                                        createdAt: Option[String],
                                        databasePath: Option[String],
                                        deletionProtection: Option[Boolean],
                                        description: Option[String],
                                        documentApiEndpoint: Option[String],
                                        folderId: Option[String],
                                        labels: Option[Map[String, Option[String]]],
                                        locationId: Option[String],
                                        name: String,
                                        serverlessDatabase: Option[Set[ServerlessDatabase]],
                                        status: Option[String],
                                        tlsEnabled: Option[Boolean],
                                        ydbApiEndpoint: Option[String],
                                        ydbFullEndpoint: Option[String]
                                      )

case class YandexYdbTopic(
                           consumer: Option[List[Consumer]],
                           databaseEndpoint: String,
                           description: Option[String],
                           meteringMode: Option[String],
                           name: String,
                           partitionsCount: Option[Int],
                           retentionPeriodMs: Option[Int],
                           supportedCodecs: Option[List[Option[String]]]
                         )

case class YaxisSettings(
                          left: Option[List[Left]],
                          right: Option[List[Right]]
                        )

case class Zonal(
                  subnetId: Option[String],
                  zone: Option[String]
                )

case class Zonal(
                  zone: Option[String]
                )

case class Zone(
                 id: Option[String]
               )

case class Zookeeper(
                      resources: Option[List[Resources]]
                    )