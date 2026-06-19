# API Usage

This API is provided for mod developpers to make modded block entities compatible with obe.

## Make your modded block entities compatible with OBE

### Case 1: Extending a vanilla block entity

In case of extending a vanilla block entity (e.g. iron chests that have bigger storage), here are the steps to follow in order to make them compatible with OBE:

1. Register your block entity's type in one of the existing groups using RegistryApi#registerBlockEntityType (e.g. for the iron chests, you should do: RegistryApi.registerBlockEntityType(blockEntityType, "chest")).

2. Register a material provider for the block entity's type using RegistryApi#registerMaterialProvider.

### Case 2: Adding a completly new block entity

This is not fully possible using only the API yet, some functions are there to help, but you'll also need to look at the mod's code and mixin at some places (mainly BlockStateModelSetMixin)

1. Register a new group for your block entity type(s) using RegistryApi#registerGroup

2. Register your block entity's type inside of that group using RegistryApi#registerBlockEntityType

3. Inside the intializer(s) of your block entity, use RegistryApi#registerSupportedBlockEntity

4. Mixin inside of BlockStateModelSetMixin and add your own group to the list

5. Inside your block entity, use RenderModeManager#setRenderModeDelayed to change dynamically change whether the block entity should be meshed or not

6. Inside your block entity's renderer, you'll have to add checks to know weither to display it or not (see ChestRendererMixin)

### Note

The API is provided on an "AS IS" and "AS AVAILABLE" basis. 

The maintainer(s) of this API assume no responsibility or liability for any actions taken by developers using this API.
This API is not guaranteed to meet your specific requirements or expectation, to be uninterrupted, timely, secure, accurate, reliable, or completely error-free.

Any use of the API is strictly at the developer’s sole discretion and risk. Under no circumstances shall the maintainer(s) be liable for instances where the API is utilized to create, distribute, transmit, or render content that is illegal, offensive, or otherwise in violation of applicable domestic or international laws. Developers bear exclusive responsibility for ensuring that their use and integration of the API comply with all relevant legal frameworks, regulatory requirements, and third-party intellectual property rights.