<script lang="ts">
    import Modal from "../common/modal/Modal.svelte";
    import ButtonSetting from "../common/setting/ButtonSetting.svelte";
    import IconTextInput from "../common/setting/IconTextInput.svelte";
    import {connectToServer} from "../../../integration/rest";
    import {setItem} from "../../../integration/persistent_storage";

    export let visible: boolean;

    let address = localStorage.getItem("multiplayer_direct_connect_address") ?? "";

    $: disabled = validateInput(address);

    function validateInput(address: string): boolean {
        return address.trim().length === 0;
    }

    async function connect() {
        if (disabled) {
            return;
        }
        address = address.trim();
        visible = false;
        await setItem("multiplayer_direct_connect_address", address)
        await connectToServer(address);
    }
</script>

<Modal bind:visible={visible} title="直连">
    <IconTextInput title="地址" icon="server" bind:value={address}/>
    <ButtonSetting title="加入服务器" on:click={connect} {disabled} listenForEnter={true} inset={true}/>
</Modal>
