这是目前后端的 api 接口，只看一次就行，后面会变化，目前大部分都是管理端的接口，客户端的很少，只有一个获取商品价格的，有需求你和我一起开发

**注意:**

* 所有需要认证的接口，都需要在请求头中加入 `Authorization: Bearer <YOUR_JWT_TOKEN>`。
* 成功的响应通常会被包裹在一个标准结构中，如：
  ```json
  {
    "success": true,
    "code": 0,
    "msg": "成功",
    "data": {
      // 实际数据
    }
  }
  ```
  如果接口没有明确的返回数据（例如 `void` 方法），`data` 字段可能不存在或为 `null`。
* 分页接口的 `data` 字段通常包含分页信息，例如 `records` (当前页数据列表), `total` (总记录数), `size` (每页数量), `current` (当前页码), `pages` (总页数)。
* 日期时间格式默认为 `yyyy-MM-dd HH:mm:ss`。

-----

# API 文档 - MallJnm

## 目录

1.  [认证 (Auth)](/search?q=%23%E8%AE%A4%E8%AF%81-auth)
2.  [管理员 (Admin)](/search?q=%23%E7%AE%A1%E7%90%86%E5%91%98-admin)
3.  [客户 (Customer)](/search?q=%23%E5%AE%A2%E6%88%B7-customer)
4.  [客户商品价格 (Customer Price)](/search?q=%23%E5%AE%A2%E6%88%B7%E5%95%86%E5%93%81%E4%BB%B7%E6%A0%BC-customer-price)
5.  [文件上传 (File)](/search?q=%23%E6%96%87%E4%BB%B6%E4%B8%8A%E4%BC%A0-file)
6.  [前端商品接口 (Frontend Product)](/search?q=%23%E5%89%8D%E7%AB%AF%E5%95%86%E5%93%81%E6%8E%A5%E5%8F%A3-frontend-product)
7.  [客户组 (Group)](/search?q=%23%E5%AE%A2%E6%88%B7%E7%BB%84-group)
8.  [客户组商品价格 (Group Price)](/search?q=%23%E5%AE%A2%E6%88%B7%E7%BB%84%E5%95%86%E5%93%81%E4%BB%B7%E6%A0%BC-group-price)
9.  [商品分类 (Product Category)](/search?q=%23%E5%95%86%E5%93%81%E5%88%86%E7%B1%BB-product-category)
10. [商品 (Product)](/search?q=%23%E5%95%86%E5%93%81-product)
11. [验证码 (Verify Image)](/search?q=%23%E9%AA%8C%E8%AF%81%E7%A0%81-verify-image)

-----

## 1\. 认证 (Auth)

### 1.1 用户登录

* **POST** `/login`
* **描述**: 用户（管理员或客户）登录系统。
* **认证**: 否
* **请求体**: `application/json`
  ```json
  {
    "username": "your_username", // 用户名 (对于客户是账户account)
    "password": "your_password", // 密码
    "userType": "ADMIN" // 或 "CUSTOMER"
    // "verifyKey": "image_verify_key", // (可选) 管理员登录时可能需要验证码的key
    // "verifyCode": "image_verify_code" // (可选) 管理员登录时输入的验证码
  }
  ```
* **成功响应**: `200 OK`
  ```json
  {
    "success": true,
    "code": 0,
    "msg": "成功",
    "data": {
      "token": "jwt_token_string",
      "user": { // 用户信息，结构根据 userType 而定
        // Admin.java 或 Users.java (Customer) 的字段
        "id": "user_id",
        "username": "actual_username"
        // ... 其他字段 (密码通常不返回)
      },
      "roles": "USER_TYPE_AS_STRING" // 例如 "ADMIN" 或 "CUSTOMER"
    }
  }
  ```

-----

## 2\. 管理员 (Admin)

基路径: `/admin`

### 2.1 获取管理员列表

* **GET** `/`
* **描述**: 分页获取管理员列表。
* **认证**: 是
* **查询参数**:
    * `index` (int, 可选, 默认 1): 页码。
    * `size` (int, 可选, 默认 10): 每页数量。
    * `username` (String, 可选): 按用户名模糊查询。
* **成功响应**: `200 OK` (分页数据，包含 `Admin` 对象列表)
  ```json
  {
    // ... (标准分页结构)
    "records": [
      {
        "id": "string",
        "username": "string",
        // "userType": "ADMIN", // 注意：原Admin模型没有userType，但登录服务实现中会设置
        "createTime": "yyyy-MM-dd HH:mm:ss"
      }
    ]
  }
  ```

### 2.2 更新管理员信息

* **PUT** `/{id}`
* **描述**: 更新指定 ID 的管理员信息。
* **认证**: 是
* **路径参数**:
    * `id` (String): 管理员 ID。
* **请求体**: `application/json` (`Admin` 对象)
  ```json
  {
    "username": "new_username"
    // password 字段会被忽略
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 2.3 删除管理员

* **DELETE** `/{id}`
* **描述**: 删除指定 ID 的管理员。
* **认证**: 是
* **路径参数**:
    * `id` (String): 管理员 ID。
* **成功响应**: `200 OK` (无数据内容)

### 2.4 添加管理员

* **POST** `/`
* **描述**: 添加新的管理员。
* **认证**: 是
* **请求体**: `application/json` (`Admin` 对象)
  ```json
  {
    "username": "admin_username",
    "password": "admin_password"
    // userType 会被后端设置为 "ADMIN" (基于登录服务和通常实践，尽管Admin模型没此字段)
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 2.5 重置管理员密码

* **PATCH** `/resetPassword`
* **描述**: 重置指定管理员的密码。
* **认证**: 是
* **查询参数**:
    * `id` (String): 管理员 ID。
    * `newPassword` (String): 新密码。
* **成功响应**: `200 OK` (无数据内容)

-----

## 3\. 客户 (Customer)

基路径: `/customer`

### 3.1 分页查询客户列表

* **GET** `/`
* **描述**: 分页获取客户列表，可带查询条件。
* **认证**: 是
* **查询参数**:
    * `pageNum` (int, 可选, 默认 1): 页码。
    * `pageSize` (int, 可选, 默认 10): 每页数量。
    * `name` (String, 可选): 按客户名称模糊查询。
    * `account` (String, 可选): 按客户登录账号模糊查询。
    * `groupId` (String, 可选): 按客户组 ID 查询。
    * `status` (Integer, 可选): 按状态查询 (0-禁用, 1-启用)。
* **成功响应**: `200 OK` (分页数据，包含 `CustomerVO` 对象列表)
  ```json
  {
    // ... (标准分页结构)
    "records": [
      {
        "id": "string",
        "name": "string",
        "account": "string",
        "status": 0, // 或 1
        "createTime": "yyyy-MM-dd HH:mm:ss",
        "updateTime": "yyyy-MM-dd HH:mm:ss",
        "groupId": "string",
        "groupName": "string"
      }
    ]
  }
  ```

### 3.2 创建客户

* **POST** `/`
* **描述**: 创建新客户。
* **认证**: 是
* **请求体**: `application/json` (`Customer` 对象)
  ```json
  {
    "name": "customer_name",
    "account": "customer_account",
    "password": "customer_password",
    "openid": "wechat_openid", // (可选)
    "groupId": "group_id_string" // (可选)
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 3.3 获取客户详情

* **GET** `/{id}`
* **描述**: 获取指定 ID 的客户详情。
* **认证**: 是
* **路径参数**:
    * `id` (String): 客户 ID。
* **成功响应**: `200 OK` (`Customer` 对象，密码字段为 null)
  ```json
  {
    "id": "string",
    "name": "string",
    "account": "string",
    "openid": "string",
    "status": 0, // 或 1
    "groupId": "string",
    "createTime": "yyyy-MM-dd HH:mm:ss",
    "updateTime": "yyyy-MM-dd HH:mm:ss"
  }
  ```

### 3.4 更新客户信息

* **PUT** `/{id}`
* **描述**: 更新指定 ID 的客户信息 (密码和账号字段在请求体中会被忽略)。
* **认证**: 是
* **路径参数**:
    * `id` (String): 客户 ID。
* **请求体**: `application/json` (`Customer` 对象)
  ```json
  {
    "name": "new_customer_name",
    "openid": "new_wechat_openid",
    "status": 1,
    "groupId": "new_group_id"
    // password 和 account 字段在此处无效
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 3.5 删除客户

* **DELETE** `/{id}`
* **描述**: 删除指定 ID 的客户。
* **认证**: 是
* **路径参数**:
    * `id` (String): 客户 ID。
* **成功响应**: `200 OK` (无数据内容)

### 3.6 批量删除客户

* **DELETE** `/batch`
* **描述**: 批量删除客户。
* **认证**: 是
* **请求体**: `application/json` (字符串数组，包含客户 ID)
  ```json
  ["id1", "id2", "id3"]
  ```
* **成功响应**: `200 OK` (无数据内容)

### 3.7 禁用客户

* **PATCH** `/{id}/disable`
* **描述**: 禁用指定 ID 的客户。
* **认证**: 是
* **路径参数**:
    * `id` (String): 客户 ID。
* **成功响应**: `200 OK` (无数据内容)

### 3.8 启用客户

* **PATCH** `/{id}/enable`
* **描述**: 启用指定 ID 的客户。
* **认证**: 是
* **路径参数**:
    * `id` (String): 客户 ID。
* **成功响应**: `200 OK` (无数据内容)

### 3.9 重置客户密码

* **PATCH** `/{id}/reset-password`
* **描述**: 重置指定 ID 客户的密码。
* **认证**: 是
* **路径参数**:
    * `id` (String): 客户 ID。
* **查询参数**:
    * `newPassword` (String): 新密码。
* **成功响应**: `200 OK` (无数据内容)

### 3.10 检查账号是否存在

* **GET** `/check-account`
* **描述**: 检查指定的客户登录账号是否存在。
* **认证**: 是 (根据 `SpringSecurityConfig`，此接口未明确放行)
* **查询参数**:
    * `account` (String): 要检查的客户登录账号。
* **成功响应**: `200 OK`
  ```json
  // data 字段为 boolean 值
  // true: 账号已存在
  // false: 账号不存在
  {
      "success": true,
      "code": 0,
      "msg": "成功",
      "data": true
  }
  ```

-----

## 4\. 客户商品价格 (Customer Price)

基路径: `/customer_price`

### 4.1 获取某客户的商品价格列表

* **GET** `/`
* **描述**: 分页获取指定客户的商品及对应的专属价格。
* **认证**: 是
* **查询参数**:
    * `index` (int, 可选, 默认 1): 页码。
    * `size` (int, 可选, 默认 10): 每页数量。
    * `name` (String, 可选): 按商品名称模糊查询。
    * `cid` (String, **必需**): 客户 ID。
    * `code` (String, 可选): 按商品编码模糊查询。
* **成功响应**: `200 OK` (分页数据，包含 `ProductWithCustomerPriceVO` 对象列表)
  ```json
  {
    // ... (标准分页结构)
    "records": [
      {
        "id": "string", // CustomerProductPrice的ID
        "customerId": "string",
        "productId": "string", // 注意：VO中productId是String，模型中是Long
        "customPrice": 100.50,
        "createTime": "yyyy-MM-dd HH:mm:ss",
        "updateTime": "yyyy-MM-dd HH:mm:ss",
        "productName": "string",
        "productCode": "string",
        "description": "string",
        "mainImage": "image_url_string",
        "standardPrice": 120.00,
        "stock": 100
      }
    ]
  }
  ```

### 4.2 保存客户商品价格

* **POST** `/`
* **描述**: 为客户设置特定商品的专属价格。
* **认证**: 是
* **请求体**: `application/json` (`CustomerProductPrice` 对象)
  ```json
  {
    "customerId": "string",
    "productId": "long", // 商品ID
    "customPrice": 99.99
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 4.3 更新客户商品价格

* **PUT** `/`
* **描述**: 更新已有的客户商品价格记录。
* **认证**: 是
* **请求体**: `application/json` (`CustomerProductPrice` 对象)
  ```json
  {
    "id": "customer_product_price_id_string", // 必需
    "customerId": "string", // 通常不更新，或用于校验
    "productId": "long",    // 通常不更新，或用于校验
    "customPrice": 98.88
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 4.4 删除客户商品价格

* **DELETE** `/{id}`
* **描述**: 删除指定的客户商品价格记录。
* **认证**: 是
* **路径参数**:
    * `id` (String): 客户商品价格记录的 ID。
* **成功响应**: `200 OK` (无数据内容)

-----

## 5\. 文件上传 (File)

### 5.1 MinIO 图片上传

* **POST** `/image`
* **描述**: 上传图片到 MinIO。
* **认证**: 是 (根据 `SpringSecurityConfig`，此接口未明确放行)
* **请求参数**: `multipart/form-data`
    * `file` (MultipartFile): 图片文件。
* **成功响应**: `200 OK`
  ```json
  // data 字段为上传成功后的文件名 (例如: uuid.jpg)
  {
    "success": true,
    "code": 0,
    "msg": "成功",
    "data": "new_file_name.jpg"
  }
  ```

### 5.2 获取图片 (MinIO)

* **GET** `/image/{name}`
* **描述**: 根据文件名获取图片。
* **认证**: 否
* **路径参数**:
    * `name` (String): 图片文件名 (包含后缀)。
* **成功响应**: `200 OK` (图片二进制流，Content-Type 根据图片类型而定)

### 5.3 删除图片 (MinIO)

* **DELETE** `/image/{name}`
* **描述**: 删除 MinIO中的图片。
* **认证**: 是
* **路径参数**:
    * `name` (String): 图片文件名。
* **成功响应**: `200 OK` (无数据内容)

### 5.4 AliOSS 文件上传

* **POST** `/upload`
* **描述**: 上传文件到阿里云 OSS。
* **认证**: 否
* **请求参数**: `multipart/form-data`
    * `file` (MultipartFile): 要上传的文件。
* **成功响应**: `200 OK`
  ```json
  // data 字段为上传成功后的文件访问 URL
  {
    "success": true,
    "code": 0,
    "msg": "成功",
    "data": "https://bucket_name.endpoint/file_name"
  }
  ```

-----

## 6\. 前端商品接口 (Frontend Product)

基路径: `/frontend`

### 6.1 获取分类及商品列表 (前端展示)

* **GET** `/categories-with-products`
* **描述**: 获取所有可见的商品分类，以及每个分类下的已上架商品列表，价格会根据当前登录用户（如果已登录）动态计算。
* **认证**: 否
* **成功响应**: `200 OK` (返回 `List<CategoryWithProductsVO>`)
  ```json
  {
    "success": true,
    "code": 0,
    "msg": "成功",
    "data": [
      {
        "categoryId": "string",
        "categoryName": "string",
        "sortOrder": 0,
        "description": "string",
        "isShow": true,
        "products": [
          {
            "id": "string", // 商品ID
            "name": "string", // 商品名称
            "code": "string", // 商品编码
            "description": "string", // 商品描述
            "mainImage": "image_url_string", // 主图
            "displayPrice": 100.00, // 最终展示价格
            "stock": 10, // 库存
            "categoryId": "string", // 分类ID
            "categoryName": "string", // 分类名称
            "sortOrder": 0, // 商品排序
            "isPublished": true // 是否上架
          }
          // ... more products
        ]
      }
      // ... more categories
    ]
  }
  ```

-----

## 7\. 客户组 (Group)

基路径: `/group`

### 7.1 创建客户组

* **POST** `/`
* **描述**: 创建新的客户组。
* **认证**: 是
* **请求体**: `application/json` (`CustomerGroup` 对象)
  ```json
  {
    "name": "group_name",
    "description": "group_description" // (可选)
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 7.2 获取客户组详情

* **GET** `/{id}`
* **描述**: 获取指定 ID 的客户组详情。
* **认证**: 是
* **路径参数**:
    * `id` (String): 客户组 ID。
* **成功响应**: `200 OK` (`CustomerGroup` 对象)
  ```json
  {
    "id": "string",
    "name": "string",
    "description": "string",
    "createTime": "yyyy-MM-dd HH:mm:ss",
    "updateTime": "yyyy-MM-dd HH:mm:ss"
  }
  ```

### 7.3 更新客户组

* **PUT** `/{id}`
* **描述**: 更新指定 ID 的客户组信息。
* **认证**: 是
* **路径参数**:
    * `id` (String): 客户组 ID。
* **请求体**: `application/json` (`CustomerGroup` 对象)
  ```json
  {
    "name": "new_group_name",
    "description": "new_group_description"
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 7.4 删除客户组

* **DELETE** `/{id}`
* **描述**: 删除指定 ID 的客户组 (如果该组下仍有客户，则无法删除)。
* **认证**: 是
* **路径参数**:
    * `id` (Integer): 客户组 ID。
* **成功响应**: `200 OK` (无数据内容)

### 7.5 分页查询客户组列表

* **GET** `/` (与 `GET /{id}` 不同，此接口用于列表查询)
* **描述**: 分页获取客户组列表。
* **认证**: 是
* **查询参数**:
    * `index` (int, 可选, 默认 1): 页码。
    * `size` (int, 可选, 默认 10): 每页数量。
    * `name` (String, 可选): 按客户组名称模糊查询。
* **成功响应**: `200 OK` (分页数据，包含 `CustomerGroup` 对象列表)

### 7.6 查询客户组内或外的客户列表

* **GET** `/customers`
* **描述**: 分页查询属于特定客户组的客户，或未分配给任何组的客户。
* **认证**: 是
* **查询参数**:
    * `pageNum` (int, 可选, 默认 1): 页码。
    * `pageSize` (int, 可选, 默认 10): 每页数量。
    * `name` (String, 可选): 按客户名称模糊查询。
    * `account` (String, 可选): 按客户账号模糊查询。
    * `gid` (String, 可选): 客户组 ID。
    * `flag` (String, 可选, 默认 "1"):
        * `1`: 查询属于 `gid` 的客户。
        * `0`: 查询未分配客户组的客户 (此时 `gid` 参数可忽略)。
* **成功响应**: `200 OK` (分页数据，包含 `Customer` 对象列表)

### 7.7 批量添加客户到指定组

* **POST** `/customers/batch/{gid}`
* **描述**: 将一批客户添加到指定的客户组。
* **认证**: 是
* **路径参数**:
    * `gid` (String): 客户组 ID。
* **请求体**: `application/json` (字符串数组，包含客户 ID)
  ```json
  ["customerId1", "customerId2"]
  ```
* **成功响应**: `200 OK` (无数据内容)

### 7.8 批量从指定组移除客户

* **PUT** `/customers/batch/{gid}`
* **描述**: 将一批客户从指定的客户组中移除 (将其 groupId 设置为 null)。
* **认证**: 是
* **路径参数**:
    * `gid` (String): 客户组 ID。
* **请求体**: `application/json` (字符串数组，包含客户 ID)
  ```json
  ["customerId1", "customerId2"]
  ```
* **成功响应**: `200 OK` (无数据内容)

### 7.9 从组中移除单个客户

* **DELETE** `/customer`
* **描述**: 将单个客户从其所属的客户组中移除 (将其 groupId 设置为 null)。
* **认证**: 是
* **查询参数**:
    * `gid` (String): 客户当前所属的客户组 ID。
    * `id` (String): 客户 ID。
* **成功响应**: `200 OK` (无数据内容)

### 7.10 获取所有客户组列表 (用于下拉)

* **GET** `/all`
* **描述**: 获取所有客户组的列表，通常用于表单下拉选择。
* **认证**: 是
* **成功响应**: `200 OK` (返回 `List<CustomerGroup>`)

-----

## 8\. 客户组商品价格 (Group Price)

基路径: `/group_price`

### 8.1 获取某客户组的商品价格列表

* **GET** `/`
* **描述**: 分页获取指定客户组的商品及对应的专属价格。
* **认证**: 是
* **查询参数**:
    * `index` (int, 可选, 默认 1): 页码。
    * `size` (int, 可选, 默认 10): 每页数量。
    * `name` (String, 可选): 按商品名称模糊查询。
    * `gid` (String, **必需**): 客户组 ID。
    * `code` (String, 可选): 按商品编码模糊查询。
* **成功响应**: `200 OK` (分页数据，包含 `ProductWithGroupPriceVO` 对象列表)
  ```json
  {
    // ... (标准分页结构)
    "records": [
      {
        "id": "string", // GroupProductPrice的ID
        "groupId": "string",
        "productId": "string",
        "customPrice": 100.50,
        "createTime": "yyyy-MM-dd HH:mm:ss",
        "updateTime": "yyyy-MM-dd HH:mm:ss",
        "productName": "string",
        "productCode": "string",
        "description": "string",
        "mainImage": "image_url_string",
        "standardPrice": 120.00,
        "stock": 100
      }
    ]
  }
  ```

### 8.2 保存客户组商品价格

* **POST** `/`
* **描述**: 为客户组设置特定商品的专属价格。
* **认证**: 是
* **请求体**: `application/json` (`GroupProductPrice` 对象)
  ```json
  {
    "groupId": "string",
    "productId": "string", // 商品ID
    "customPrice": 99.99
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 8.3 更新客户组商品价格

* **PUT** `/`
* **描述**: 更新已有的客户组商品价格记录。
* **认证**: 是
* **请求体**: `application/json` (`GroupProductPrice` 对象)
  ```json
  {
    "id": "group_product_price_id_string", // 必需
    "groupId": "string", // 通常不更新，或用于校验
    "productId": "string", // 通常不更新，或用于校验
    "customPrice": 98.88
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 8.4 删除客户组商品价格

* **DELETE** `/{id}`
* **描述**: 删除指定的客户组商品价格记录。
* **认证**: 是
* **路径参数**:
    * `id` (String): 客户组商品价格记录的 ID。
* **成功响应**: `200 OK` (无数据内容)

-----

## 9\. 商品分类 (Product Category)

基路径: `/category`

### 9.1 分页获取商品分类列表

* **GET** `/`
* **描述**: 分页获取商品分类列表。
* **认证**: 是
* **查询参数**:
    * `page` (Integer, 可选, 默认 1): 页码。
    * `size` (Integer, 可选, 默认 10): 每页数量。
    * `name` (String, 可选): 按分类名称模糊查询。
* **成功响应**: `200 OK` (分页数据，包含 `ProductCategory` 对象列表)
  ```json
  {
    // ... (标准分页结构)
    "records": [
      {
        "id": "string",
        "name": "string",
        "sortOrder": 0,
        "description": "string",
        "isShow": true,
        "createTime": "yyyy-MM-dd HH:mm:ss",
        "updateTime": "yyyy-MM-dd HH:mm:ss"
      }
    ]
  }
  ```

### 9.2 添加商品分类

* **POST** `/`
* **描述**: 添加新的商品分类。
* **认证**: 是
* **请求体**: `application/json` (`ProductCategory` 对象)
  ```json
  {
    "name": "category_name",
    "sortOrder": 10,
    "description": "category_description",
    "isShow": true
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 9.3 更新商品分类

* **PUT** `/{id}`
* **描述**: 更新指定 ID 的商品分类信息。
* **认证**: 是
* **路径参数**:
    * `id` (String): 分类 ID。
* **请求体**: `application/json` (`ProductCategory` 对象)
  ```json
  {
    "name": "new_category_name",
    "sortOrder": 5,
    "description": "new_description",
    "isShow": false
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 9.4 删除商品分类

* **DELETE** `/{id}`
* **描述**: 删除指定 ID 的商品分类。
* **认证**: 是
* **路径参数**:
    * `id` (String): 分类 ID。
* **成功响应**: `200 OK` (无数据内容)

### 9.5 调整分类排序

* **PATCH** `/{id}/sort`
* **描述**: 更新指定分类的排序权重。
* **认证**: 是
* **路径参数**:
    * `id` (String): 分类 ID。
* **查询参数**:
    * `sortOrder` (Integer): 新的排序权重。
* **成功响应**: `200 OK` (无数据内容)

### 9.6 切换分类显示状态

* **PATCH** `/{id}/status`
* **描述**: 切换指定分类的显示/隐藏状态。
* **认证**: 是
* **路径参数**:
    * `id` (String): 分类 ID。
* **成功响应**: `200 OK` (无数据内容)

### 9.7 获取所有可见分类

* **GET** `/visible`
* **描述**: 获取所有设置为可见的商品分类列表，通常用于前端下拉选择。
* **认证**: 是 (根据 `SpringSecurityConfig`，此接口未明确放行，但常理应为公开或客户可访问)
* **成功响应**: `200 OK` (返回 `List<ProductCategory>`)

-----

## 10\. 商品 (Product)

基路径: `/product`

### 10.1 分页查询商品列表

* **GET** `/`
* **描述**: 分页获取商品列表，可按名称和分类 ID 查询。
* **认证**: 是
* **查询参数**:
    * `index` (int, 可选, 默认 1): 页码。
    * `size` (int, 可选, 默认 10): 每页数量。
    * `name` (String, 可选): 按商品名称模糊查询。
    * `categoryId` (Integer, 可选): 按分类 ID 查询。
* **成功响应**: `200 OK` (分页数据，包含 `Product` 对象列表)
  ```json
  {
    // ... (标准分页结构)
    "records": [
      {
        "id": "string",
        "categoryId": "integer",
        "name": "string",
        "code": "string",
        "description": "string",
        "images": "url1,url2,url3",
        "price": 199.99,
        "costPrice": 99.99,
        "stock": 100,
        "sales": 50,
        "isPublished": true,
        "sortOrder": 0,
        "createTime": "yyyy-MM-dd HH:mm:ss",
        "updateTime": "yyyy-MM-dd HH:mm:ss"
      }
    ]
  }
  ```

### 10.2 添加商品

* **POST** `/`
* **描述**: 添加新商品。
* **认证**: 是
* **请求体**: `application/json` (`ProductVO` 对象)
  ```json
  {
    "categoryId": "integer",
    "name": "product_name",
    "code": "product_code",
    "description": "product_description",
    "images": "image_url1.jpg,image_url2.png", // 逗号分隔的图片文件名
    "price": 299.00,
    "costPrice": 150.00,
    "stock": 200,
    "isPublished": true,
    "sortOrder": 10,
    "usedImages": ["image_url1.jpg", "image_url2.png"] // 实际使用的图片列表 (文件名)
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 10.3 更新商品

* **PUT** `/{id}`
* **描述**: 更新指定 ID 的商品信息。
* **认证**: 是
* **路径参数**:
    * `id` (String): 商品 ID。
* **请求体**: `application/json` (`ProductVO` 对象)
  ```json
  {
    "categoryId": "integer",
    "name": "updated_product_name",
    "code": "updated_product_code",
    // ... 其他 Product 字段
    "images": "new_image1.jpg,old_image2.png",
    "usedImages": ["new_image1.jpg"], // 新增或仍然使用的图片
    "noUsedImages": ["old_image1.jpg"] // 不再使用的图片
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 10.4 删除商品

* **DELETE** `/{id}`
* **描述**: 删除指定 ID 的商品。
* **认证**: 是
* **路径参数**:
    * `id` (String): 商品 ID。
* **请求体**: `application/json` (`ProductVO` 对象，主要用于传递 `noUsedImages` 以更新文件使用状态)
  ```json
  {
    "noUsedImages": ["image1.jpg", "image2.jpg"] // 商品关联的图片列表，将被标记为未使用
  }
  ```
* **成功响应**: `200 OK` (无数据内容)

### 10.5 商品上架

* **PATCH** `/{id}/publish`
* **描述**: 将指定 ID 的商品上架。
* **认证**: 是
* **路径参数**:
    * `id` (String): 商品 ID。
* **成功响应**: `200 OK` (无数据内容)

### 10.6 商品下架

* **PATCH** `/{id}/unpublish`
* **描述**: 将指定 ID 的商品下架。
* **认证**: 是
* **路径参数**:
    * `id` (String): 商品 ID。
* **成功响应**: `200 OK` (无数据内容)

-----

## 11\. 验证码 (Verify Image)

### 11.1 获取图片验证码

* **GET** `/verify`
* **描述**: 生成并获取图片验证码。
* **认证**: 否
* **查询参数**:
    * `width` (int, 可选, 默认 70): 图片宽度。
    * `height` (int, 可选, 默认 30): 图片高度。
    * `codeCount` (int, 可选, 默认 4): 验证码字符数量。
    * `family` (String, 可选): 字体名称。
* **成功响应**: `200 OK`
  ```json
  {
    "success": true,
    "code": 0,
    "msg": "成功",
    "data": {
      "key": "random_uuid_key_for_cache", // 用于后续验证的 key
      "image": "data:image/jpeg;base64,base64_encoded_image_string" // Base64 编码的图片
    }
  }
  ```

-----