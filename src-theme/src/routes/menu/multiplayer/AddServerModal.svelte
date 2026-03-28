<script lang="ts">
    import Modal from "../common/modal/Modal.svelte";
    import SingleSelect from "../common/setting/select/SingleSelect.svelte";
    import ButtonSetting from "../common/setting/ButtonSetting.svelte";
    import IconTextInput from "../common/setting/IconTextInput.svelte";
    import {addServer as restAddServer} from "../../../integration/rest";
    import {createEventDispatcher} from "svelte";

    export let visible: boolean;

    const dispatch = createEventDispatcher();

    let name = "Minecraft Server";
    let address = "";
    let resourcePackPolicy = "Prompt";

    $: disabled = validateInput(address, name);
    $: address = address.trim();

    function validateInput(address: string, name: string): boolean {
        return address.length === 0 || name.length === 0;
    }

    async function addServer() {
        if (disabled) {
            return;
        }
        await restAddServer(name, address, resourcePackPolicy);
        dispatch("serverAdd");
        cleanUp();
        visible = false;
    }

    function cleanUp() {
        name = "Minecraft Server";
        address = "";
        resourcePackPolicy = "";
    }
</script>

<Modal bind:visible={visible} title="添加服务器" on:close={cleanUp}>
    <IconTextInput title="名称" icon="info" bind:value={name}/>
    <IconTextInput title="地址" icon="server" bind:value={address}/>
    <SingleSelect title="服务器资源包" options={["提示", "启用", "禁用"]} bind:value={resourcePackPolicy}/>
    <ButtonSetting title="添加服务器" on:click={addServer} {disabled} listenForEnter={true} inset={true}/>
</Modal>
