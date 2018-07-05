namespace java com.lioncorp.service.thrift.iface


struct UserRequest {
	1:optional list<string> ids
}

struct Material {
	1:optional string id
	2:optional string material
}

struct MaterialResponse {
    1:optional list<string> ids
    2:optional list<Material> materials
    3:optional i32 code
    4:optional string msg
}

service MaterialService {
    MaterialResponse getMaterial(1:UserRequest userRequest)
    MaterialResponse getMaterialWithCache(1:UserRequest userRequest)
    i32 getHealthStatus()
}