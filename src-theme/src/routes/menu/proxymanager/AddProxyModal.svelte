<script lang="ts">
    import Modal from "../common/modal/Modal.svelte";
    import IconTextInput from "../common/setting/IconTextInput.svelte";
    import SwitchSetting from "../common/setting/SwitchSetting.svelte";
    import ButtonSetting from "../common/setting/ButtonSetting.svelte";
    import {addProxy as addProxyRest, browse} from "../../../integration/rest";
    import {listen} from "../../../integration/ws";
    import SingleSelect from "../common/setting/select/SingleSelect.svelte";

    export let visible: boolean;

    $: disabled = validateInput(requiresAuthentication, hostPort, username, password);
    $: {
        if (!requiresAuthentication) {
            username = "";
            password = "";
        }
    }

    let requiresAuthentication = false;
    let proxyType = "SOCKS5";
    let hostPort = "";
    let username = "";
    let password = "";
    let forwardAuthentication = false;
    let loading = false;

    function validateInput(requiresAuthentication: boolean, host: string, username: string, password: string): boolean {
        let valid = /.+:[0-9]+/.test(host);

        if (requiresAuthentication) {
            valid &&= username.length > 0 && password.length > 0;
        }

        return !valid;
    }

    async function addProxy() {
        if (disabled) {
            return;
        }
        const [host, port] = hostPort.split(":");

        loading = true;
        await addProxyRest(host, parseInt(port), username, password, proxyType, forwardAuthentication);
    }

    listen("proxyCheckResult", () => {
        loading = false;
        visible = false;
        cleanup();
    });

    function cleanup() {
        requiresAuthentication = false;
        proxyType = "SOCKS5";
        hostPort = "";
        username = "";
        password = "";
        forwardAuthentication = false;
    }
</script>

<Modal title="添加代理" bind:visible={visible} on:close={cleanup}>
    <IconTextInput title="主机:端口" icon="server" pattern=".+:[0-9]+" bind:value={hostPort}/>
    <SingleSelect title="代理类型" options={["HTTP", "SOCKS5"]} bind:value={proxyType}/>
    <SwitchSetting title="需要认证" bind:value={requiresAuthentication}/>
    {#if requiresAuthentication}
        <IconTextInput title="用户名" icon="user" bind:value={username}/>
        <IconTextInput title="密码" icon="lock" type="password" bind:value={password}/>
    {/if}
    <SwitchSetting title="转发 Microsoft 认证" bind:value={forwardAuthentication}/>
    <ButtonSetting title="添加代理" {disabled} on:click={addProxy} listenForEnter={true} {loading}/>
    <ButtonSetting title="获取代理" on:click={() => browse("PROXY_WEBSITE")} secondary={true}/>
</Modal>
